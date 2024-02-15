import i18n from "./i18n.js";
import {useState} from "react";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {routes} from "@/common/routes/server.jsx";
import Root from "@/states/Root/index.js";
import {TokenProvider} from "@/common/contexts/Token";
import {ServerProvider} from "@/common/contexts/Server";
import Login from "@/states/Login";

export default () => {
    const [translationsLoaded, setTranslationsLoaded] = useState(false);

    const router = createBrowserRouter([
        {path: "/login", element: <Login />},
        {path: "/", element: <ServerProvider><Root /></ServerProvider>, children: routes}
    ]);

    i18n.on("initialized", () => setTranslationsLoaded(true));

    if (!translationsLoaded) return <></>;

    return (
        <>
            <TokenProvider>
                <RouterProvider router={router} />
            </TokenProvider>
        </>
    )
}