import { FaTimes, FaLock, FaShare, FaFileSignature } from "react-icons/fa";
import { useState } from "react";
import { Link } from "react-router-dom";

const File = ({ file, onDelete, onLock, onShare, onRename, onOpen }) => {
    const [edit, setEdit] = useState(false);
    const [id, setId] = useState(0);
    const [value, setValue] = useState("");
    const onRenameLocal = (idl) => {
        setEdit(!edit);
        onRename(value, id);
        // onRename(e.target.value, file.id)
    };

    const onRenameLocal2 = (value, id) => {
        setValue(value);
        setId(id);
    };

    return (
        <div className="file">
            <h3>
                {edit ? (
                    <input
                        type="text"
                        // value={file.name}
                        onChange={(e) =>
                            onRenameLocal2(e.target.value, file.id)
                        }
                    />
                ) : (
                    <Link
                        to={`./files/${file.id}`}
                        onClick={() => onOpen(file.id)}
                    >
                        <div className="item">{file.name}</div>
                    </Link>
                )}
                <div className="item">
                    <p>{file.size}</p>
                </div>

                <div className="item">{file.location}</div>

                {/* <FaLock
                    style={{ color: "black", cursor: "pointer" }}
                    onClick={() => onLock(file)}
                /> */}
                <div className="item">
                    <FaFileSignature
                        style={{ color: "black", cursor: "pointer" }}
                        onClick={() => onRenameLocal(file.id)}
                    />
                    <FaShare
                        style={{ color: "black", cursor: "pointer" }}
                        onClick={() => onShare(file.id)}
                    />
                    <FaTimes
                        style={{ color: "red", cursor: "pointer" }}
                        onClick={() => {
                            onDelete(file.id);
                        }}
                    />
                </div>
            </h3>
        </div>
    );
};

export default File;
