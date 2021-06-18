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
                    `https://localhost:8443/api/fileDrop/sharedWith?file_path=${file.owner}/${file.name}`,
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
    const [ldummy, setLDummy] = useState();

    if (typeof file.location !== "undefined") {
        var coords = file.location.split(" ");
        var latitude = parseFloat(coords[0]).toFixed(2);
        var longitude = parseFloat(coords[1]).toFixed(2);
    }

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

    const onShareLocal = async (lid) => {
        //onShare(lid, input);

        const data = new FormData();
        data.append("file_path", username + "/" + file.address);
        data.append("email", input);
        await axios.post(`https://localhost:8443/api/fileDrop/share`, data, {
            headers: { Authorization: token },
        });

        setDummy2((dummy2) => dummy2 + 1);
        forceUpdate();
        onGoBack();
        setState2({ lol: "NANANA" });
    };

    const onUnshareLocal = async (lid) => {
        console.log(
            `username + "/" + file.address`,
            username + "/" + file.address
        );
        await axios.delete(
            `https://localhost:8443/api/fileDrop/unshare?file_path=${
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
                    `https://localhost:8443/api/fileDrop/sharedWith?file_path=${file.owner}/${file.name}`,
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
                    console.log(`Unable to get sharedWith, error: `, error)
                );
        }
        setLDummy(dummy);
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
                    : Array.isArray(file) && !file[0].owner.startsWith(username)
                    ? "sharedFile"
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
                            <FaFolder size="30px" className="fileIcon" />
                            {file[0].name}
                        </div>
                    </Link>
                ) : //
                // if this is not a folder, but it's a go-back button
                file.name === ".." ? (
                    <Link to={"/"} onClick={() => onRestore()}>
                        <div className="item">
                            <FaFolderOpen size="30px" className="fileIcon" />
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
                                <FaFileWord size="30px" className="fileIcon" />
                            ) : (
                                <FaFileImage size="30px" className="fileIcon" />
                            )}
                            {file.name}
                        </div>
                    </Link>
                )}
                <div className="item">
                    {file.type === "dir" ? null : <p>{file.size}</p>}
                </div>

                <div className="item">
                    {file.type === "dir" ? null : (
                        <p>
                            {file.location == "0, 0, 0, 0"
                                ? "not available"
                                : typeof file.location !== "undefined"
                                ? `[${latitude}, ${longitude}]`
                                : ""}
                        </p>
                    )}
                </div>

                <div className="item">
                    {file.type === "dir" || Array.isArray(file) ? null : (
                        <>
                            {typeof file.owner !== "undefined" ? (
                                file.owner.startsWith(username) ? (
                                    <>
                                        <FaFileSignature
                                            size="30px"
                                            style={{
                                                color: "black",
                                                cursor: "pointer",
                                                margin: "5px",
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
                                                        size="30px"
                                                        style={{
                                                            color: "black",
                                                            cursor: "pointer",
                                                            margin: "5px",
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
                                            size="30px"
                                            style={{
                                                color: "red",
                                                cursor: "pointer",
                                                margin: "5px",
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
                                        size="30px"
                                        style={{
                                            color: "black",
                                            cursor: "pointer",
                                        }}
                                        onClick={() => onRenameLocal(file.id)}
                                    />

                                    <Popup
                                        size="30px"
                                        open={false}
                                        trigger={
                                            <button className="btn2">
                                                <FaShare
                                                    size="30px"
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
                                        size="30px"
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
