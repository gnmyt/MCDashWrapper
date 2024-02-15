import {Alert, Avatar, Box, Chip, CircularProgress, IconButton, Snackbar, Stack, Typography} from "@mui/material";
import {Dns, MoreVert} from "@mui/icons-material";
import PurpurImage from "@/common/assets/software/purpur.webp";
import PaperImage from "@/common/assets/software/paper.webp";
import SpigotImage from "@/common/assets/software/spigot.webp";
import {useEffect, useRef, useState} from "react";
import {isDev, PROXY_URL} from "@/common/utils/RequestUtil.js";
import ServerInfo from "@/states/Root/pages/Overview/components/Server/components/ServerInfo";
import ServerMenu from "@/states/Root/pages/Overview/components/Server/components/ServerMenu";

export const Server = ({uuid, configuration, status}) => {
    const [menuOpen, setMenuOpen] = useState(false);
    const menuRef = useRef(null);

    const [alert, setAlert] = useState(null);

    const frameRef = useRef(null);
    const serverRef = useRef(null);
    const [serverInfo, setServerInfo] = useState({});
    const [frameOpen, setFrameOpen] = useState(false);
    const [frameLoaded, setFrameLoaded] = useState(false);

    useEffect(() => {
        if (!frameOpen) setFrameLoaded(false);
    }, [frameOpen]);

    useEffect(() => {
        if (isDev) return () => {};

        const interval = setInterval(() => {
            if (frameRef.current) {
                frameRef.current.contentWindow.document.querySelectorAll(".MuiToolbar-regular").forEach((el) => {
                    clearInterval(interval);
                    el.style.display = "none";
                });

                frameRef.current.contentWindow.document.querySelectorAll(".MuiDivider-root").forEach((el) => {
                    el.style.display = "none";
                });
            }
        }, 200);

        return () => clearInterval(interval);
    }, [frameOpen]);

    useEffect(() => {
        if (serverInfo === null || Object.keys(serverInfo).length === 0) setFrameOpen(false);
    }, [serverInfo]);

    const onServerClick = () => {
        if (serverRef.current === null) return;
        if (serverRef.current.contains(document.activeElement)) return;

        if (serverInfo !== null && Object.keys(serverInfo).length > 0) {
            setFrameOpen(!frameOpen);
        }
    }

    return (
        <>
            <Snackbar open={alert !== null} autoHideDuration={6000} onClose={() => setAlert(null)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert severity={alert !== null ? alert.severity : "info"}>{alert !== null ? alert.message : ""}</Alert>
            </Snackbar>
            <ServerMenu menuOpen={menuOpen} menuRef={menuRef} setMenuOpen={setMenuOpen} uuid={uuid} setAlert={setAlert}
                        status={status} config={configuration}/>
            <Box key={uuid} backgroundColor="background.darker" borderRadius={2} p={2} m={1}
                 style={{cursor: serverInfo !== null && Object.keys(serverInfo).length > 0 ? "pointer" : "default"}}
                 boxShadow={5} gap={2} ref={serverRef} onClick={onServerClick}>

                <Stack direction="row" gap={2} alignItems="center" justifyContent="space-between"
                       marginBottom={frameOpen ? 2 : 0}>
                    <Stack direction="row" gap={2} alignItems="center">
                        <Avatar sx={{width: 45, height: 45, backgroundColor: "primary.main"}}>
                            <Dns/>
                        </Avatar>
                        <Stack direction="column" gap={1} sx={{width: {xs: "100%", md: "100%", lg: "25rem"}}}>
                            <Stack direction="row" gap={1} alignItems="center">
                                <img alt={configuration.type} style={{width: 25, height: 25}}
                                     src={configuration.type === "purpur" ? PurpurImage : configuration.type === "paper" ? PaperImage : SpigotImage}/>
                                <Typography variant="h6">{configuration.name}</Typography>
                                <Chip label={configuration.version} color="primary" size="small"/>
                            </Stack>

                            <Typography variant="body2">{configuration.description}</Typography>
                        </Stack>

                        <ServerInfo status={status} uuid={uuid} serverInfo={serverInfo} setServerInfo={setServerInfo}
                                    setAlert={setAlert}/>
                    </Stack>
                    <Stack direction="row" gap={2} alignItems="center">
                        <IconButton color="primary" size="large" id="server-menu" onClick={() => setMenuOpen(!menuOpen)}
                                    ref={menuRef}>
                            <MoreVert/>
                        </IconButton>
                        <Box width={10} height={50} borderRadius={2}
                             bgcolor={status === "ONLINE" ? "success.main" : "error.main"}/>
                    </Stack>
                </Stack>

                {frameOpen && !frameLoaded && (
                    <Stack direction="row" gap={2} alignItems="center" justifyContent="center" height={500}>
                        <CircularProgress/>
                    </Stack>
                )}

                {frameOpen && (
                    <iframe src={PROXY_URL + "/" + uuid + "/"} width="100%" height={frameLoaded ? 600 : 0}
                            onLoad={() => setInterval(() => setFrameLoaded(true), 600)}
                            style={{border: "none", borderRadius: 20}} ref={frameRef}/>
                )}
            </Box>
        </>
    )
}