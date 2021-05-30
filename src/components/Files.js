import File from "./File";
import * as _ from "lodash";
import { useState } from "react";

const Files = ({
    files,
    onDelete,
    onShare,
    onRename,
    edit,
    sortBy,
    ascDesc,
    onOpen,
    onFolder,
    onRestore,
    username,
    fileOwner,
    token,
    dummy,
    onGoBack,
}) => {
    var [a, setA] = useState(0);

    return (
        <>
            {_.orderBy(files, sortBy, ascDesc).map((file, i) => (
                <File
                    key={i}
                    file={file}
                    onDelete={onDelete}
                    onShare={onShare}
                    onRename={onRename}
                    onOpen={onOpen}
                    onFolder={onFolder}
                    onRestore={onRestore}
                    username={username}
                    fileOwner={fileOwner}
                    token={token}
                    dummy={dummy}
                    onGoBack={onGoBack}
                />
            ))}
        </>
    );
};

export default Files;
