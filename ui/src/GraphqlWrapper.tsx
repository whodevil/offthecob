import React from "react";
import {GraphiQL} from "graphiql";
import { createGraphiQLFetcher } from '@graphiql/toolkit';

const fetcher = createGraphiQLFetcher({
    url: 'https://api-staging.offthecob.info/graphql',
});

const GraphqlWrapper: React.FC = () => {
    return (
        <GraphiQL fetcher={fetcher} />
    )
}

export default GraphqlWrapper
