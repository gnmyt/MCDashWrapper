import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    InputAdornment,
    Stack,
    TextField
} from "@mui/material";
import {t} from "i18next";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import {ServerContext} from "@/common/contexts/Server";

export default ({open, setOpen, uuid, setAlert, current}) => {

    const {updateServer} = useContext(ServerContext);
    const [memory, setMemory] = useState(current || 4096);

    const updateMemory = async () => {
        setOpen(false);
        await patchRequest("server/", {uuid, memory});
        updateServer();
        setAlert({severity: "success", message: t("server.dialog.memory.success")});
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{t("server.dialog.memory.title")}</DialogTitle>
            <DialogContent>
                <Stack marginTop={1} maxWidth="12rem">
                    <TextField label={t("server.dialog.memory.memory")} variant="outlined" fullWidth value={memory}
                               type="number" onChange={(e) => setMemory(e.target.value)}
                               inputProps={{min: 512, step: 512}}
                               InputProps={{endAdornment: <InputAdornment position={"end"}>{t("server.dialog.memory.unit")}</InputAdornment>}}/>
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("common.cancel")}</Button>
                <Button onClick={updateMemory} color="primary">{t("server.dialog.memory.button")}</Button>
            </DialogActions>
        </Dialog>
    );
}