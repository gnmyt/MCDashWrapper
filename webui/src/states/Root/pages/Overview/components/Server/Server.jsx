import {Avatar, Box, Button, Chip, CircularProgress, IconButton, Stack, Tooltip, Typography} from "@mui/material";
import {AvTimer, Bolt, Dns, Group, PowerOff, PowerSettingsNew, Share} from "@mui/icons-material";
import PurpurImage from "@/common/assets/software/purpur.webp";
import PaperImage from "@/common/assets/software/paper.webp";
import SpigotImage from "@/common/assets/software/spigot.webp";
import {useContext, useEffect, useRef, useState} from "react";
import {isDev, postRequest, PROXY_URL, proxyRequest} from "@/common/utils/RequestUtil.js";
import {ServerContext} from "@/common/contexts/Server/index.js";
import {t} from "i18next";

export const Server = ({uuid, configuration, status}) => {

    const {updateServer} = useContext(ServerContext);

    const frameRef = useRef(null);
    const serverRef = useRef(null);
    const [serverInfo, setServerInfo] = useState({});
    const [frameOpen, setFrameOpen] = useState(false);
    const [frameLoaded, setFrameLoaded] = useState(false);

    useEffect(() => {
        if (!frameOpen) setFrameLoaded(false);
    }, [frameOpen]);

    useEffect(() => {
        if (isDev) return () => {
        };

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

    const updateServerInfo = () => {
        proxyRequest(uuid + "/api/stats/")
            .then((response) => {
                if (response.status === 200) return response.json();
                throw new Error("Server is offline");
            })
            .then((data) => setServerInfo(data))
            .catch(() => setServerInfo(null));
    }

    const startServer = async () => {
        await postRequest("server/start", {uuid});
        updateServer();
    }

    const stopServer = async () => {
        await postRequest("server/stop", {uuid});
        updateServer();
    }

    const onServerClick = () => {
        if (serverRef.current === null) return;
        if (serverRef.current.contains(document.activeElement)) return;

        if (serverInfo !== null && Object.keys(serverInfo).length > 0) {
            setFrameOpen(!frameOpen);
        }
    }

    useEffect(() => {
        updateServerInfo();

        const interval = setInterval(() => updateServerInfo(), 5000);

        return () => clearInterval(interval);
    }, []);

    return (
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

                    <Stack direction="row" gap={2} alignItems="center">
                        {serverInfo === null && (
                            <Button variant="outlined" color="error" onClick={() => console.log("test")}
                                    startIcon={<PowerOff/>}>{t("server.not_connected")}
                            </Button>)}
                        {serverInfo !== null && Object.keys(serverInfo).length === 0 && (
                            <Button variant="outlined" color="warning" onClick={() => console.log("test")}
                                    startIcon={<Bolt/>}>{t("server.loading")}
                            </Button>)}
                        {serverInfo !== null && Object.keys(serverInfo).length > 0 && (
                            <Stack direction="row" gap={3} alignItems="center"
                                   sx={{display: {xs: "none", md: "none", lg: "flex"}}}>
                                <Stack direction="row" gap={1} alignItems="center">
                                    <Group/>
                                    <Typography
                                        variant="body2">{serverInfo.online_players} / {serverInfo.max_players}</Typography>
                                </Stack>
                                <Stack direction="row" gap={1} alignItems="center">
                                    <AvTimer/>
                                    <Typography variant="body2">{serverInfo.tps}</Typography>
                                </Stack>
                                <Stack direction="row" gap={1} alignItems="center">
                                    <Button variant="outlined" color="info" onClick={() => console.log("test")}
                                            startIcon={<Share/>}>{t("server.share")}
                                    </Button>
                                </Stack>
                            </Stack>
                        )}
                    </Stack>

                </Stack>
                <Stack direction="row" gap={2} alignItems="center">
                    <Tooltip title={status === "OFFLINE" ? t("server.start") : t("server.stop")}>
                        <IconButton color="primary" size="large"
                                    onClick={() => status === "OFFLINE" ? startServer() : stopServer()}>
                            <PowerSettingsNew/>
                        </IconButton>
                    </Tooltip>
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
    )
}