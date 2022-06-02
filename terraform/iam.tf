#resource "aws_iam_user" "theagainagain" {
#  name = "theagainagain"
#}
#
#resource "aws_iam_user_policy" "lb_ro" {
#  name = "test"
#  user = aws_iam_user.theagainagain.name
#
#  policy = <<EOF
#{
#  "Version": "2012-10-17",
#  "Statement": [
#    {
#      "Sid": "AllowListObjects",
#      "Effect": "Allow",
#      "Action": [
#        "s3:ListBucket",
#        "s3:GetBucketLocation"
#      ],
#      "Resource": "${aws_s3_bucket.theagainagain_assets.arn}"
#    },
#    {
#      "Sid": "AllowObjectsCRUD",
#      "Effect": "Allow",
#      "Action": [
#        "s3:DeleteObject",
#        "s3:GetObject",
#        "s3:PutObject",
#        "s3:AbortMultipartUpload",
#        "s3:ListMultipartUploadParts"
#      ],
#      "Resource": "${aws_s3_bucket.theagainagain_assets.arn}/*"
#    }
#  ]
#}
#EOF
#
#}

