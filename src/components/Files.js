import File from "./File";
import * as _ from "lodash";

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
}) => {
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
                />
            ))}
        </>
    );
};

export default Files;
