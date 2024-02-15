import {
    Alert,
    Button,
    Checkbox, Dialog, DialogActions,
    DialogContent,
    DialogTitle,
    FormControlLabel, Link,
    MenuItem,
    Select,
    Stack,
    TextField
} from "@mui/material";

import SpigotImage from "@/common/assets/software/spigot.webp";
import PaperImage from "@/common/assets/software/paper.webp";
import PurpurImage from "@/common/assets/software/purpur.webp";
import {request} from "@/common/utils/RequestUtil.js";
import {useContext, useEffect, useState} from "react";
import {ServerContext} from "@/common/contexts/Server/index.js";
import {t} from "i18next";
import {Trans} from "react-i18next";
import {VersionContext} from "@/common/contexts/Version";

export const CreationDialog = ({dialogOpen, setDialogOpen}) => {

    const EULA_URL = "https://www.minecraft.net/en-us/eula";

    const {updateServer} = useContext(ServerContext);
    const versions = useContext(VersionContext);

    const [creating, setCreating] = useState(false);

    const [errorMessage, setErrorMessage] = useState("");

    const [serverName, setServerName] = useState("");
    const [serverDescription, setServerDescription] = useState("");
    const [serverSoftware, setServerSoftware] = useState("spigot");
    const [serverVersion, setServerVersion] = useState(versions[0]);
    const [eulaAccepted, setEulaAccepted] = useState(false);

    const createServer = async () => {
        if (creating) return;

        if (serverName === "") {
            setErrorMessage(t("server.creation.error.name"));
            return;
        }

        if (!eulaAccepted) {
            setErrorMessage(t("server.creation.error.eula"));
            return;
        }

        setErrorMessage("");

        setCreating(true);

        const {valid} = await (await request("check_version?version=" + serverVersion + "&software=" + serverSoftware, "GET", {}, {}, false)).json();

        if (!valid) {
            setCreating(false);
            setErrorMessage(t("server.creation.error.not_supported", {software: serverSoftware}));
            return;
        }

        await request("server/setup", "POST", {
            name: serverName, description: serverDescription || t("server.creation.default_description"),
            type: serverSoftware, version: serverVersion,
            memory: 4096, autoStart: false
        }, {}, false);

        setCreating(false);
        setDialogOpen(false);
        updateServer();
    }

    useEffect(() => {
        if (errorMessage !== "") setErrorMessage("");
    }, [serverName, serverDescription, serverSoftware, serverVersion, eulaAccepted]);


    return (
        <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
            <DialogTitle>{t("server.creation.title")}</DialogTitle>
            <DialogContent>

                {errorMessage && <Alert severity="error" sx={{marginBottom: 2, maxWidth: "20rem"}}>{errorMessage}</Alert>}

                <Stack marginTop={1} marginBottom={2} maxWidth="20rem">
                    <TextField label={t("server.creation.name")} variant="outlined" fullWidth value={serverName}
                               onChange={(e) => setServerName(e.target.value)}/>
                </Stack>

                <Stack marginTop={1} marginBottom={2} maxWidth="20rem">
                    <TextField label={t("server.creation.description")} variant="outlined" multiline rows={2} fullWidth
                               value={serverDescription} onChange={(e) => setServerDescription(e.target.value)}/>
                </Stack>

                <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2}
                       marginTop={1} marginBottom={2} maxWidth="20rem">
                    <Select variant="outlined" fullWidth value={serverSoftware}
                            onChange={(e) => setServerSoftware(e.target.value)}>
                        <MenuItem value="spigot">
                            <Stack direction="row" gap={1}>
                                <img src={SpigotImage} alt="Spigot" width={24} height={24}/>
                                Spigot
                            </Stack>
                        </MenuItem>
                        <MenuItem value="paper">
                            <Stack direction="row" gap={1}>
                                <img src={PaperImage} alt="Paper" width={24} height={24}/>
                                Paper
                            </Stack>
                        </MenuItem>
                        <MenuItem value="purpur">
                            <Stack direction="row" gap={1}>
                                <img src={PurpurImage} alt="Purpur" width={24} height={24}/>
                                Purpur
                            </Stack>
                        </MenuItem>
                    </Select>
                    <Select variant="outlined" fullWidth value={serverVersion}
                            onChange={(e) => setServerVersion(e.target.value)}>
                        {versions.map((version) => <MenuItem key={version} value={version}>{version}</MenuItem>)}
                    </Select>
                </Stack>

                <FormControlLabel control={<Checkbox/>} checked={eulaAccepted}
                                    onChange={(e) => setEulaAccepted(e.target.checked)}
                                  label={<Trans components={{Link: <Link href={EULA_URL} target="_blank"/>}}>
                                      server.creation.eula</Trans>}/>
            </DialogContent>
            <DialogActions>
                {!creating && <Button onClick={() => setDialogOpen(false)}>{t("common.cancel")}</Button>}
                {!creating && <Button onClick={createServer} color="primary">{t("common.create")}</Button>}
                {creating && <Button disabled>{t("server.creation.creating")}</Button>}
            </DialogActions>
        </Dialog>
    )
}