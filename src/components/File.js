import {
    FaTimes,
    FaShare,
    FaFileSignature,
    FaFolder,
    FaFileWord,
    FaFileImage,
    FaFolderOpen,
} from "react-icons/fa";
import { useState, useEffect, useReducer, useRef } from "react";
import { Link } from "react-router-dom";
import Popup from "reactjs-popup";
import axios from "axios";
import { ConsoleHandler } from "logging-library";

const useComponentWillMount = (func) => {
    const willMount = useRef(true);

    if (willMount.current) func();

    willMount.current = false;
};

const File = ({
    file,
    onDelete,
    onLock,
    onShare,
    onRename,
    onOpen,
    onFolder,
    onRestore,
    fileOwner,
    username,
    token,
    dummy,
    onGoBack,
}) => {
    useComponentWillMount(() => {
        console.log("willMount");
        if (
            typeof file.name !== "undefined" &&
            typeof file.owner !== "undefined"
        ) {
            axios
                .get(
                    `http://localhost:8080/api/fileDrop/sharedWith?file_path=${file.owner}/${file.name}`,
                    {
                        headers: { Authorization: token },
                    }
                )
                .then((res) => res.data)
                .then((data) => {
                    var emails = data.map((entry) => entry.email);
                    setShared(emails);
                })
                .catch((error) =>
                    //setShared({"dummy"});
                    console.log(`Unable to get sharedWith, error: `, error)
                );
        }
    });
    const [edit, setEdit] = useState(false);
    const [id, setId] = useState(0);
    const [value, setValue] = useState("");
    const [input, setInput] = useState("");
    const [shared, setShared] = useState([]);
    const [showList, setShowList] = useState(false);
    const [dummy2, setDummy2] = useState(0);
    const [, forceUpdate] = useReducer((x) => x + 1, 0);
    const [state, setState] = useState();
    const [state2, setState2] = useState();

    const onRenameLocal = (idl) => {
        setEdit(!edit);
    };

    const onRenameLocal2 = (value, id) => {
        setValue(value);
        setId(id);
    };

    const onRenameLocal3 = (value, id) => {
        setEdit(false);
        onRename(value, id);
        setDummy2((dummy2) => dummy2 + 1);
    };

    const onShareLocal2 = (input, lid) => {
        setInput(input);
    };

    const onShareLocal = (lid) => {
        //onShare(lid, input);

        const data = new FormData();
        data.append("file_path", username + "/" + file.address);
        data.append("email", input);
        axios.post(`http://localhost:8080/api/fileDrop/share`, data, {
            headers: { Authorization: token },
        });

        setDummy2((dummy2) => dummy2 + 1);
        forceUpdate();
        onGoBack();
        setState2({ lol: "NANANA" });
    };

    const onUnshareLocal = (lid) => {
        console.log(
            `username + "/" + file.address`,
            username + "/" + file.address
        );
        axios.delete(
            `http://localhost:8080/api/fileDrop/unshare?file_path=${
                username + "/" + file.address
            }`,
            {
                headers: {
                    Authorization: token,
                },
            }
        );
        setDummy2((dummy2) => dummy2 + 1);
        forceUpdate();
        onGoBack();
        setState({ lol: "NANANA" });
    };

    const onListLocal = (lid) => {
        setShowList(!showList);
    };

    useEffect(() => {
        if (
            typeof file.name !== "undefined" &&
            typeof file.owner !== "undefined"
        ) {
            axios
                .get(
                    `http://localhost:8080/api/fileDrop/sharedWith?file_path=${file.owner}/${file.name}`,
                    {
                        headers: { Authorization: token },
                    }
                )
                .then((res) => res.data)
                .then((data) => {
                    var emails = data.map((entry) => entry.email);
                    setShared(emails);
                })
                .catch((error) =>
                    //setShared({"dummy"});
                    console.log(`Unable to get sharedWith, error: `, error)
                );
        }
    }, []);

    console.log(`file.owner`, file.owner);

    return (
        <div
            className={
                typeof file.owner !== "undefined"
                    ? file.owner.startsWith(username)
                        ? shared.length > 1
                            ? "exposedFile"
                            : "file"
                        : "sharedFile"
                    : "file"
            }
        >
            <h3>
                {edit ? ( // If name is being edited
                    <input
                        type="text"
                        onChange={(e) =>
                            onRenameLocal2(e.target.value, file.id)
                        }
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                onRenameLocal3(value, id);
                            }
                        }}
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
                            {typeof file.owner !== "undefined" ? (
                                file.owner.startsWith(username) ? (
                                    <>
                                        <FaFileSignature
                                            style={{
                                                color: "black",
                                                cursor: "pointer",
                                            }}
                                            onClick={() =>
                                                onRenameLocal(file.id)
                                            }
                                        />

                                        <Popup
                                            open={false}
                                            trigger={
                                                <button className="btn2">
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
                                                Share/Unshare with who?
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
                                                    className="shareButton"
                                                    onClick={() => {
                                                        forceUpdate();
                                                        onShareLocal(file.id);
                                                    }}
                                                >
                                                    Share
                                                </button>
                                                <button
                                                    className="unshareButton"
                                                    onClick={() => {
                                                        forceUpdate();
                                                        onUnshareLocal(file.id);
                                                    }}
                                                >
                                                    UnShare
                                                </button>
                                                <button
                                                    className="listButton"
                                                    onClick={() =>
                                                        onListLocal(file.id)
                                                    }
                                                >
                                                    List
                                                </button>
                                                {showList
                                                    ? shared.join("\n")
                                                    : null}
                                            </div>
                                        </Popup>

                                        <FaTimes
                                            style={{
                                                color: "red",
                                                cursor: "pointer",
                                            }}
                                            onClick={() => {
                                                onDelete(file.id);
                                            }}
                                        />
                                    </>
                                ) : null
                            ) : (
                                // THIS IS COMMANDS if file.owner is undefined
                                <>
                                    <FaFileSignature
                                        style={{
                                            color: "black",
                                            cursor: "pointer",
                                        }}
                                        onClick={() => onRenameLocal(file.id)}
                                    />

                                    <Popup
                                        open={false}
                                        trigger={
                                            <button className="btn2">
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
                                                className="shareButton"
                                                onClick={() => {
                                                    forceUpdate();
                                                    onShareLocal(file.id);
                                                }}
                                            >
                                                Share
                                            </button>
                                            <button
                                                className="unshareButton"
                                                onClick={() => {
                                                    forceUpdate();
                                                    onUnshareLocal(file.id);
                                                }}
                                            >
                                                UnShare
                                            </button>
                                            <button
                                                className="listButton"
                                                onClick={() =>
                                                    onListLocal(file.id)
                                                }
                                            >
                                                List
                                            </button>
                                            {showList
                                                ? shared.join("\n")
                                                : null}
                                        </div>
                                    </Popup>

                                    <FaTimes
                                        style={{
                                            color: "red",
                                            cursor: "pointer",
                                        }}
                                        onClick={() => {
                                            onDelete(file.id);
                                        }}
                                    />
                                </>
                            )}
                        </>
                    )}
                </div>
            </h3>
        </div>
    );
};

export default File;
