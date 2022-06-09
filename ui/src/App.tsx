import React from 'react'
import { BrowserRouter as Router, Route } from 'react-router-dom'

import { ApolloProvider } from '@apollo/react-hooks'
import './App.css'
import Home from './Home'

import ApolloClient from 'apollo-boost'
import GraphqlWrapper from "./GraphqlWrapper";

const client = new ApolloClient({
  uri: 'https://api-staging.offthecob.info/graphql',
  request: operation => {
    operation.setContext({
      headers: {
        'X-Requested-With': 'apollo',
      },
    })
  },
})

const App: React.FC = () => {
  return (
    <ApolloProvider client={client}>
      <Router>

        <div>
          <Route path="/" exact component={Home}/>
          <Route path="/graphiql" exact component={GraphqlWrapper} />
        </div>

      </Router>
    </ApolloProvider>
  )
}

export default App
