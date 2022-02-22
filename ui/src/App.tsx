import React from 'react'
import { BrowserRouter as Router, Route } from 'react-router-dom'

import { ApolloProvider } from '@apollo/react-hooks'
import './App.css'
import Home from './Home'

import ApolloClient from 'apollo-boost'

const client = new ApolloClient({
  uri: '/graphql',
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
        </div>

      </Router>
    </ApolloProvider>
  )
}

export default App
