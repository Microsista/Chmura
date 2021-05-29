import {
    FaTimes,
    FaShare,
    FaFileSignature,
    FaFolder,
    FaFileWord,
    FaFileImage,
    FaFolderOpen,
} from "react-icons/fa";
import { useState } from "react";
import { Link } from "react-router-dom";
import Popup from "reactjs-popup";

const File = ({
    file,
    onDelete,
    onLock,
    onShare,
    onRename,
    onOpen,
    onFolder,
    onRestore,
}) => {
    const [edit, setEdit] = useState(false);
    const [id, setId] = useState(0);
    const [value, setValue] = useState("");
    const [input, setInput] = useState("");

    const onRenameLocal = (idl) => {
        setEdit(!edit);
        onRename(value, id);
    };

    const onRenameLocal2 = (value, id) => {
        setValue(value);
        setId(id);
    };

    const onShareLocal = (lid) => {
        onShare(lid, input);
    };

    const onShareLocal2 = (input, lid) => {
        setInput(input);
    };

    return (
        <div className="file">
            <h3>
                {edit ? ( // If name is being edited
                    <input
                        type="text"
                        // value={file.name}
                        onChange={(e) =>
                            onRenameLocal2(e.target.value, file.id)
                        }
                    />
                ) : // else if this is a folder
                Array.isArray(file) ? (
                    <Link to={"/"} onClick={() => onFolder(file)}>
                        <div className="item">
                            <FaFolder className="fileIcon" />
                            {file[0].name}
                        </div>
                    </Link>
                ) : //
                // if this is not a folder, but it's a go-back button
                file.name === ".." ? (
                    <Link to={"/"} onClick={() => onRestore()}>
                        <div className="item">
                            <FaFolderOpen className="fileIcon" />
                            {file.name}
                        </div>
                    </Link>
                ) : (
                    // else
                    <Link
                        to={`./files/${file.id}`}
                        onClick={() => onOpen(file.id)}
                    >
                        <div className="item">
                            {file.name.endsWith("txt") ? (
                                <FaFileWord className="fileIcon" />
                            ) : (
                                <FaFileImage className="fileIcon" />
                            )}
                            {file.name}
                        </div>
                    </Link>
                )}
                <div className="item">
                    {file.type === "dir" ? null : <p>{file.size}</p>}
                </div>

                <div className="item">
                    {file.type === "dir" ? null : <p>{file.location}</p>}
                </div>

                <div className="item">
                    {file.type === "dir" || Array.isArray(file) ? null : (
                        <>
                            <FaFileSignature
                                style={{ color: "black", cursor: "pointer" }}
                                onClick={() => onRenameLocal(file.id)}
                            />

                            <Popup
                                open={false}
                                trigger={
                                    <button class="btn2">
                                        <FaShare
                                            style={{
                                                color: "black",
                                                cursor: "pointer",
                                            }}
                                        />
                                    </button>
                                }
                                position="right center"
                            >
                                <div>
                                    Share with who?
                                    <input
                                        type="text"
                                        onChange={(e) =>
                                            onShareLocal2(
                                                e.target.value,
                                                file.id
                                            )
                                        }
                                    />
                                    <button
                                        className="deleteButton"
                                        onClick={() => onShareLocal(file.id)}
                                    >
                                        Share
                                    </button>
                                </div>
                            </Popup>

                            <FaTimes
                                style={{ color: "red", cursor: "pointer" }}
                                onClick={() => {
                                    onDelete(file.id);
                                }}
                            />
                        </>
                    )}
                </div>
            </h3>
        </div>
    );
};

export default File;
