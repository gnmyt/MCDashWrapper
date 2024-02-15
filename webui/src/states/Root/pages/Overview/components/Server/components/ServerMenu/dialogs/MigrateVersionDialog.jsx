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
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import {ServerContext} from "@/common/contexts/Server";
import SpigotImage from "@/common/assets/software/spigot.webp";
import PaperImage from "@/common/assets/software/paper.webp";
import PurpurImage from "@/common/assets/software/purpur.webp";

export default ({open, setOpen, uuid, setAlert, currentSoftware, currentVersion}) => {

    const {updateServer} = useContext(ServerContext);

    const [serverSoftware, setServerSoftware] = useState(currentSoftware);
    const [serverVersion, setServerVersion] = useState(currentVersion);

    const migrateVersion = async () => {
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
                        <MenuItem value="1.20.4">1.20.4</MenuItem>
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