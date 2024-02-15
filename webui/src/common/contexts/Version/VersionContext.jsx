import {createContext, useEffect, useState} from "react";

export const VersionContext = createContext({});

export const VersionProvider = ({children}) => {

    const [versions, setVersions] = useState(["1.20.1"]);

    const updateVersions = () => {
        fetch("https://piston-meta.mojang.com/mc/game/version_manifest.json")
            .then(res => res.json())
            .then(data => {
                let versions = [];
                data.versions.forEach(version => {
                    if (version.type === "release" && version.id.split(".")[1] > 8)
                        versions.push(version.id);
                });
                versions.push("1.8.8");
                setVersions(versions);
            });
    }

    useEffect(() => {
        updateVersions();
    }, []);

    return (
        <VersionContext.Provider value={versions}>
            {children}
        </VersionContext.Provider>
    );
}