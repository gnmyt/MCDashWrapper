import {ListItemIcon, Menu, MenuItem} from "@mui/material";
import {Brightness4, Brightness7, Favorite, Logout} from "@mui/icons-material";
import {useContext} from "react";
import {TokenContext} from "@/common/contexts/Token";
import {t} from "i18next";
import {SettingsContext} from "@/common/contexts/Settings";

const DONATION_URL = "https://ko-fi.com/gnmyt";

export const AccountMenu = ({menuOpen, setMenuOpen}) => {
    const {checkToken} = useContext(TokenContext);
    const {theme, updateTheme} = useContext(SettingsContext);

    const switchTheme = () => {
        setMenuOpen(false);
        updateTheme(theme === 'dark' ? 'light' : 'dark');
    }

    const logout = () => {
        setMenuOpen(false);
        localStorage.removeItem("token");
        checkToken();
    }

    const openDonation = () => {
        setMenuOpen(false);
        window.open(DONATION_URL, "_blank");
    }

    return (
        <>
            <Menu anchorEl={document.getElementById("menu")} open={menuOpen} onClose={() => setMenuOpen(false)}>
                <MenuItem onClick={openDonation}>
                    <ListItemIcon>
                        <Favorite color="error" />
                    </ListItemIcon>
                    {t("header.support_me")}
                </MenuItem>
                <MenuItem onClick={switchTheme}>
                    <ListItemIcon>
                        {theme === 'dark' ? <Brightness7 size="small" /> : <Brightness4 size="small" />}
                    </ListItemIcon>
                    {t(`header.${theme === 'dark' ? 'light' : 'dark'}_theme`)}
                </MenuItem>
                <MenuItem onClick={logout}>
                    <ListItemIcon>
                        <Logout fontSize="small"/>
                    </ListItemIcon>
                    {t("header.logout")}
                </MenuItem>
            </Menu>
        </>
    )
}