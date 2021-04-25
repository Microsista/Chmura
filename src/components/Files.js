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
}) => {
    return (
        <>
            {_.orderBy(files, sortBy, ascDesc).map((file) => (
                <File
                    key={file.id}
                    file={file}
                    onDelete={onDelete}
                    onShare={onShare}
                    onRename={onRename}
                    onOpen={onOpen}
                />
            ))}
        </>
    );
};

export default Files;
