import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";

import '@fontsource/inter/300.css';
import '@fontsource/inter/400.css';
import '@fontsource/inter/500.css';
import '@fontsource/inter/700.css';

import {SettingsProvider} from "@/common/contexts/Settings";

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <SettingsProvider>
            <App />
        </SettingsProvider>
    </React.StrictMode>
);