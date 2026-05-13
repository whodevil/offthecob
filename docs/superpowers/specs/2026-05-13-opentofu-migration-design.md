# OpenTofu Migration Design

> **Date:** 2026-05-13
> **Status:** Approved

## Goal

Migrate the `offthecob` repository from Terraform 1.2.0 to OpenTofu 1.11.7, preserving the existing S3-backed state file without manual conversion.

## Context

This repository uses Terraform 1.2.0 to manage AWS infrastructure (S3, CloudFront, Route53, ACM) for a static website. State is stored in an S3 backend (`s3://otc-tf/theagainagain/terraform.tfstate`). CI/CD runs via GitHub Actions using `hashicorp/setup-terraform@v3`.

The user has OpenTofu 1.11.7 installed locally via Homebrew.

## Approach

**Approach A: Direct State Compatibility Migration (Selected)**

OpenTofu is designed as a drop-in replacement for Terraform and maintains backward compatibility with Terraform state files. We will:

1. Update GitHub Actions to install OpenTofu instead of Terraform
2. Update all `terraform` CLI commands to `tofu`
3. Update the version constraint to reflect OpenTofu
4. Leave the S3 backend configuration unchanged
5. Verify locally that `tofu init` adopts the existing state and `tofu plan` shows no changes

## Changes

### 1. GitHub Actions Workflows

**Files:** `.github/workflows/tf-plan.yml`, `.github/workflows/tf-apply.yml`

- Replace `hashicorp/setup-terraform@v3` with `opentofu/setup-opentofu@v1`
- Replace `terraform_version: 1.2.0` with `tofu_version: "1.11.7"`
- Replace all `terraform` commands with `tofu`:
  - `terraform fmt` → `tofu fmt`
  - `terraform init` → `tofu init`
  - `terraform validate` → `tofu validate`
  - `terraform workspace select` → `tofu workspace select`
  - `terraform plan` → `tofu plan`
  - `terraform apply` → `tofu apply`

### 2. Version Constraint

**File:** `terraform/versions.tf`

- Change `required_version = ">= 1.2.0"` to `required_version = ">= 1.11.7"`

### 3. Backend Configuration

**File:** `terraform/terraform_state.tf`

- No changes. The existing S3 backend configuration remains valid.

### 4. Gitignore

**File:** `terraform/.gitignore`

- No changes. OpenTofu uses the same `.terraform` directory and `.terraform.lock.hcl` lock file.

### 5. Infrastructure Code

All `.tf` files in `terraform/`:
- No changes. OpenTofu is syntax-compatible with Terraform HCL.

## Testing / Success Criteria

- [ ] `tofu init` in `terraform/` directory completes successfully
- [ ] `tofu plan` shows **no changes** (proving state adoption is correct)
- [ ] GitHub Actions workflows run successfully with OpenTofu
- [ ] No infrastructure is recreated or modified during migration

## Risks & Mitigations

| Risk | Mitigation |
|------|-----------|
| State file format incompatibility | OpenTofu guarantees backward compatibility with Terraform state. If issues arise, we can export the state with `terraform state pull` and import with `tofu state push`. |
| Provider version drift | The lock file will be regenerated on first `tofu init`. We will review the plan output to ensure no provider upgrades trigger resource recreation. |
| CI version mismatch | Pin `tofu_version: "1.11.7"` to match the user's local version. |

## Out of Scope

- Renaming the state key or creating a backup state
- Changing any infrastructure resources
- Migrating to a different backend
- Upgrading AWS provider versions (unless required by OpenTofu)
