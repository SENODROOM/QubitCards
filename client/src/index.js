import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import App from './App';
import './styles/global.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
      <Toaster
        position="bottom-right"
        toastOptions={{
          style: {
            fontFamily: 'Nunito, sans-serif',
            fontWeight: 600,
            borderRadius: '14px',
            background: '#fff',
            color: '#1A1A2E',
            boxShadow: '0 8px 30px rgba(108,99,255,0.18)',
          },
          success: { iconTheme: { primary: '#6C63FF', secondary: '#fff' } },
        }}
      />
    </BrowserRouter>
  </React.StrictMode>
);
