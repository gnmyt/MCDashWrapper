import {
    Alert,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle, MenuItem,
    Select,
    Stack
} from "@mui/material";
import {t} from "i18next";
import {patchRequest, request} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import {ServerContext} from "@/common/contexts/Server";
import SpigotImage from "@/common/assets/software/spigot.webp";
import PaperImage from "@/common/assets/software/paper.webp";
import PurpurImage from "@/common/assets/software/purpur.webp";
import {VersionContext} from "@/common/contexts/Version/index.js";

export default ({open, setOpen, uuid, setAlert, currentSoftware, currentVersion}) => {

    const versions = useContext(VersionContext);
    const {updateServer} = useContext(ServerContext);

    const [serverSoftware, setServerSoftware] = useState(currentSoftware);
    const [serverVersion, setServerVersion] = useState(currentVersion);

    const migrateVersion = async () => {
        const {valid} = await (await request("check_version?version=" + serverVersion + "&software=" + serverSoftware, "GET", {}, {}, false)).json();

        if (!valid) {
            setAlert({severity: "error", message: t("server.creation.error.not_supported", {software: serverSoftware})});
            return;
        }

        setOpen(false);

        await patchRequest("server/", {uuid, type: serverSoftware, version: serverVersion});
        updateServer();
        setAlert({severity: "success", message: t("server.dialog.migrate.success")});
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{t("server.dialog.migrate.title")}</DialogTitle>
            <DialogContent>
                <Alert severity="warning" sx={{width: "20rem"}}>{t("server.dialog.migrate.warning")}</Alert>

                <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2}
                       marginTop={2} marginBottom={2} maxWidth="20rem">
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
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("common.cancel")}</Button>
                <Button onClick={migrateVersion} color="primary">{t("server.dialog.migrate.button")}</Button>
            </DialogActions>
        </Dialog>
    );
}