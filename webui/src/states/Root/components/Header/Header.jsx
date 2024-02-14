import {AppBar, Avatar, IconButton, Stack, Toolbar, Tooltip, Typography} from "@mui/material";
import {Menu as MenuIcon} from "@mui/icons-material";
import {useEffect} from "react";
import {sidebar} from "@/common/routes/server.jsx";
import {useLocation} from "react-router-dom";
import {t} from "i18next";

const drawerWidth = 240;

export const Header = ({toggleOpen}) => {
    const location = useLocation();

    const retrieveUsername = () => atob(localStorage.getItem("token")).split(":")[0];

    useEffect(() => {
        document.title = "MCDash - " + getTitleByPath();
    }, [location]);

    const getTitleByPath = () => {
        const route = sidebar.find((route) => location.pathname.startsWith(route.path) && route.path !== "/");
        if (route) return route.name();
        return t("nav.server");
    }


    return (
        <AppBar position="fixed" sx={{width: {sm: `calc(100% - ${drawerWidth}px)`}, ml: {sm: `${drawerWidth}px`}}}>

            <Toolbar>
                <IconButton aria-label="open drawer" edge="start" onClick={toggleOpen}
                            sx={{mr: 2, display: {sm: 'none'}}}>
                    <MenuIcon/>
                </IconButton>
                <Typography variant="h6" noWrap>{getTitleByPath()}</Typography>

                <Stack sx={{ml: "auto"}} direction="row">
                    <IconButton id="menu">
                        <Avatar sx={{width: 24, height: 24}}/>
                    </IconButton>
                </Stack>
            </Toolbar>
        </AppBar>
    )
}