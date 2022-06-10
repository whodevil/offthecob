import React from "react";
import {GraphiQL} from "graphiql"
import {createGraphiQLFetcher} from '@graphiql/toolkit'
import 'graphiql/graphiql.min.css'

const fetcher = createGraphiQLFetcher({
    url: 'https://api-staging.offthecob.info/graphql',
    headers: {
        'X-Requested-With': 'graphiql',
    }
});

const GraphqlWrapper: React.FC = () => {
    return (
        <GraphiQL fetcher={fetcher}/>
    )
}

export default GraphqlWrapper
