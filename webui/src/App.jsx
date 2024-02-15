import i18n from "./i18n.js";
import React, {useContext, useState} from "react";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {routes} from "@/common/routes/server.jsx";
import Root from "@/states/Root/index.js";
import {TokenProvider} from "@/common/contexts/Token";
import {ServerProvider} from "@/common/contexts/Server";
import Login from "@/states/Login";
import {CssBaseline, ThemeProvider} from "@mui/material";

import LightTheme from "@/common/themes/light.js";
import DarkTheme from "@/common/themes/dark.js";
import {SettingsContext} from "@/common/contexts/Settings";

export default () => {
    const [translationsLoaded, setTranslationsLoaded] = useState(false);
    const {theme} = useContext(SettingsContext);

    const router = createBrowserRouter([
        {path: "/login", element: <Login />},
        {path: "/", element: <ServerProvider><Root /></ServerProvider>, children: routes}
    ]);

    i18n.on("initialized", () => setTranslationsLoaded(true));

    if (!translationsLoaded) return <></>;

    return (
        <>
            <ThemeProvider theme={theme === "dark" ? DarkTheme : LightTheme}>
                <CssBaseline />
                <TokenProvider>
                    <RouterProvider router={router} />
                </TokenProvider>
            </ThemeProvider>

        </>
    )
}