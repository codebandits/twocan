import React from 'react';
import logo from './logo.svg';
import './App.css';
import {createTheme, ThemeProvider, Typography} from "@mui/material";

const theme = createTheme({})

const Providers: React.FC = ({children}) => (
    <ThemeProvider theme={theme}>
      {children}
    </ThemeProvider>
)

const SampleApp = () => (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo"/>
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
            className="App-link"
            href="https://reactjs.org"
            target="_blank"
            rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
);

export const App = () => (
    <Providers>
      <Typography variant="h1" textAlign="center">Twocan</Typography>
      <SampleApp/>
    </Providers>
);
