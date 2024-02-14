import {Dashboard, Dns, Share as ShareIcon} from "@mui/icons-material";
import Overview from "@/states/Root/pages/Overview";
import {t} from "i18next";
import Share from "@/states/Root/pages/Share";

export const routes = [
    {path: "/", element: <Overview />},
    {path: "/ports", element: <Share/>},
]

export const sidebar = [
    {
        path: "/",
        icon: <Dns />,
        name: () => t("nav.server")
    },
    {
        path: "/ports",
        icon: <ShareIcon/>,
        name: () => t("nav.ports")
    }
]