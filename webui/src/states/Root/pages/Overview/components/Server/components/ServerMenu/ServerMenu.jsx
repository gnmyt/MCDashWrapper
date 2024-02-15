import {Divider, ListItemIcon, ListItemText, Menu, MenuItem} from "@mui/material";
import {
    Delete,
    DriveFileRenameOutline,
    EditNote,
    ManageHistory, Memory,
    PowerSettingsNew,
    RestartAlt
} from "@mui/icons-material";
import {t} from "i18next";
import {patchRequest, postRequest} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import {ServerContext} from "@/common/contexts/Server";
import RenameDialog from "./dialogs/RenameDialog.jsx";
import UpdateDescriptionDialog from "./dialogs/UpdateDescriptionDialog.jsx";
import MigrateVersionDialog from "./dialogs/MigrateVersionDialog.jsx";
import ChangeRAMDialog from "./dialogs/ChangeRAMDialog.jsx";
import ConfirmDeleteDialog from "./dialogs/ConfirmDeleteDialog.jsx";

export const ServerMenu = ({menuOpen, menuRef, setMenuOpen, uuid, setAlert, status, config}) => {

    const [renameDialogOpen, setRenameDialogOpen] = useState(false);
    const [updateDescriptionDialogOpen, setUpdateDescriptionDialogOpen] = useState(false);
    const [migrateDialogOpen, setMigrateDialogOpen] = useState(false);
    const [changeRAMDialogOpen, setChangeRAMDialogOpen] = useState(false);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

    const {updateServer} = useContext(ServerContext);

    const startServer = async () => {
        setMenuOpen(false);
        setAlert({severity: "success", message: t("server.starting")});
        await postRequest("server/start", {uuid});

        updateServer();
    }

    const stopServer = async () => {
        setMenuOpen(false);
        setAlert({severity: "success", message: t("server.stopping")});
        await postRequest("server/stop", {uuid});

        updateServer();
    }

    const renameServer = async () => {
        setMenuOpen(false);
        setRenameDialogOpen(true);
    }

    const updateDescription = async () => {
        setMenuOpen(false);
        setUpdateDescriptionDialogOpen(true);
    }

    const migrateVersion = async () => {
        setMenuOpen(false);
        setMigrateDialogOpen(true);
    }

    const changeRAM = async () => {
        setMenuOpen(false);
        setChangeRAMDialogOpen(true);
    }

    const toggleStartOnBoot = async () => {
        setMenuOpen(false);
        setAlert({severity: "success", message: t("server.dialog.toggle_start_on_boot.success")});
        await patchRequest("server/", {uuid, autoStart: !config.autoStart});
        updateServer();
    }

    const deleteServer = async () => {
        setMenuOpen(false);
        setDeleteDialogOpen(true);
    }

    return (
        <>
            <RenameDialog open={renameDialogOpen} setOpen={setRenameDialogOpen} uuid={uuid} setAlert={setAlert}
                            current={config.name}/>
            <UpdateDescriptionDialog open={updateDescriptionDialogOpen} setOpen={setUpdateDescriptionDialogOpen}
                                        uuid={uuid} setAlert={setAlert} current={config.description}/>
            <MigrateVersionDialog open={migrateDialogOpen} setOpen={setMigrateDialogOpen} uuid={uuid} setAlert={setAlert}
                                    currentSoftware={config.type} currentVersion={config.version}/>
            <ChangeRAMDialog open={changeRAMDialogOpen} setOpen={setChangeRAMDialogOpen} uuid={uuid} setAlert={setAlert}
                                current={config.memory}/>
            <ConfirmDeleteDialog open={deleteDialogOpen} setOpen={setDeleteDialogOpen} uuid={uuid} setAlert={setAlert}/>

            <Menu open={menuOpen} anchorEl={menuRef.current} onClose={() => setMenuOpen(false)}
                  anchorOrigin={{vertical: "bottom", horizontal: "right"}} transformOrigin={{vertical: "top", horizontal: "right"}}>
                <MenuItem onClick={() => renameServer()}>
                    <ListItemIcon><DriveFileRenameOutline/></ListItemIcon>
                    <ListItemText>{t("server.rename")}</ListItemText>
                </MenuItem>

                <MenuItem onClick={() => updateDescription()}>
                    <ListItemIcon><EditNote/></ListItemIcon>
                    <ListItemText>{t("server.update_description")}</ListItemText>
                </MenuItem>

                <MenuItem onClick={() => migrateVersion()}>
                    <ListItemIcon><ManageHistory/></ListItemIcon>
                    <ListItemText>{t("server.migrate")}</ListItemText>
                </MenuItem>

                <MenuItem onClick={() => changeRAM()}>
                    <ListItemIcon><Memory/></ListItemIcon>
                    <ListItemText>{t("server.update_ram")}</ListItemText>
                </MenuItem>

                <MenuItem onClick={() => toggleStartOnBoot()}>
                    <ListItemIcon><RestartAlt/></ListItemIcon>
                    <ListItemText>{t("server." + (config.autoStart ? "disable" : "enable") + "_start_on_boot")}</ListItemText>
                </MenuItem>

                <Divider/>

                {status === "OFFLINE" && (<MenuItem onClick={startServer}><ListItemIcon><PowerSettingsNew /></ListItemIcon>
                    <ListItemText>{t("server.start")}</ListItemText>
                </MenuItem>)}
                {status === "ONLINE" && (<MenuItem onClick={stopServer}><ListItemIcon><PowerSettingsNew /></ListItemIcon>
                    <ListItemText>{t("server.stop")}</ListItemText>
                </MenuItem>)}

                <MenuItem onClick={() => deleteServer()}>
                    <ListItemIcon><Delete/></ListItemIcon>
                    <ListItemText>{t("server.delete")}</ListItemText>
                </MenuItem>
            </Menu>
        </>
    )
}