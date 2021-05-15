import Login from "./components/Login";
import SignUp from "./components/SignUp";
import logo from "./cloud.png";
import Files from "./components/Files";
import { useState, useEffect, useRef } from "react";
import {
    BrowserRouter as Router,
    Link,
    Route,
    Redirect,
} from "react-router-dom";
import Help from "./components/Help";
import Child from "./components/Child";

import {
    FaSortAmountDown,
    FaSortAmountDownAlt,
    FaSortAlphaDown,
    FaSortAlphaDownAlt,
    FaQuestion,
    FaPlusSquare,
    FaMoon,
    FaLongArrowAltDown,
} from "react-icons/fa";
import AddFile from "./components/AddFile";
import nextId from "react-id-generator";
import Popup from "reactjs-popup";
import axios from "axios";
import * as _ from "lodash";

const App = () => {
    const [showAddFile, setShowAddFile] = useState(false);
    const [username, setUsername] = useState("User");
    const [redirect, setRedirect] = useState(false);
    const [loggedin, setLoggedin] = useState(false);
    const [files, setFiles] = useState([]);
    const [sortBy, setSortBy] = useState("name");
    const [ascDesc, setAscDesc] = useState("asc");
    const [id, setId] = useState("id0");
    const [backup, setBackup] = useState([]);
    const [file, setFile] = useState();
    const [currFolder, setCurrFolder] = useState("");

    const fileDesc = (name, size, location, owner, address, type, mytype) => {
        return {
            id: nextId().slice(2, 20),
            name: name,
            size: size,
            location: location,
            owner: owner,
            address: address,
            type: type,
            mytype: mytype,
        };
    };

    const checkSubfolder = (arr, name, size, location, address) => {
        if (name.endsWith("txt"))
            arr.push(fileDesc(name, size, location, "", address, "text", "text"));
        else
            arr.push(fileDesc(name, size, location, "", address, "img", "img"));
    };

    const onLogin = async (username, password) => {
        // Set a sign in request.
        const requestOptions = {
            method: "POST",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username,
                password,
            }),
            credentials: "include",
        };

        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/signIn",
            requestOptions
        );

        // If login was successful.
        if (rawResponse.status === 200) {
            setRedirect(true);
            setLoggedin(true);
            setUsername(username);

            // Get filenames.
            const options = {
                method: "GET",
                credentials: "include",
            };

            // Initial fetch of the data to iterate over and get file sizes.
            var myData;
            await fetch("http://localhost:8080/api/fileDrop", options)
                .then((response) => response.json())
                .then((data) => {
                    myData = data;
                });

            var k = 0;
            for (var i in myData) k++;
            // Iterate over the data and retrieve file sizes.
            var sizes = new Array(k);
            for (var i in myData)
                sizes[i] = new Array(myData[i].length);
            for (var i in myData) {
                for (var j = 0; j < myData[i].length; j++) {
                    
                    var resp = await fetch(
                        `http://localhost:8080/api/fileDrop/download?file_path=Mariam/${myData[i][j]}`,
                        options
                    );
                    sizes[i][j] = resp.headers.get("content-length") / 1000;
                    console.log(sizes[i][j]);
                }
            }

            var arr = [];
            var folders = [];
            await fetch("http://localhost:8080/api/fileDrop", options).then((response) => response.json()).then((data) => {
                for (var i in data) {
                    if (i === username)
                        for (var j = 0; j < data[i].length; j++)
                            checkSubfolder(arr, data[i][j], sizes[i][j], "", data[i][j]);
                    else // if this is a subdirectory

                    for (var j = 0; j < data[i].length; j++) {
                        var folderName = i.substr(username.length + 1, i.length - 1);
                        
                            
                        else if (!folders.includes(folderName)) {
                            var lArr = [];
                            lArr.push(fileDesc(folderName, 0, "", "", folderName, "dir", "dir"));
                            for (var k in data)
                                for (var l = 0; l < data[k].length; l++) {
                                    var lFolderName = k.substr(username.length + 1, k.length - 1);
                                    var folderLocation = username + "/" + lFolderName;
                                    if (k.startsWith(folderLocation))
                                        checkSubfolder(lArr, data[k][l], sizes[k][l], "", lFolderName + "/" + data[k][l]);
                                }
                            folders.push(folderName);
                            arr.push(lArr);
                        }
                    }
                });
                }
               
            setFiles(arr);
            setBackup(arr);
        } else alert("Wrong username or password");
    };

    //
    //
    //
    const refresh = async () => {
        const requestOptions2 = {
            method: "GET",
            credentials: "include",
        };

        // Initial fetch of the data to iterate over and get file sizes.
        var myData;
        await fetch("http://localhost:8080/api/fileDrop", requestOptions2)
            .then((response) => response.json())
            .then((data) => {
                myData = data;
            });

        var k = 0;
        for (var i in myData) k++;
        // Iterate over the data and retrieve file sizes.
        var sizes = new Array(k);
        for (var i in myData) {
            sizes[i] = new Array(myData[i].length);
            //console.log(sizes[i].length);
        }
        for (var i in myData) {
            for (var j = 0; j < myData[i].length; j++) {
                const options = {
                    method: "GET",
                    credentials: "include",
                };
                var resp = await fetch(
                    `http://localhost:8080/api/fileDrop/download?file_path=Mariam/${myData[i][j]}`,
                    options
                );
                sizes[i][j] = resp.headers.get("content-length") / 1000;
                console.log(sizes[i][j]);
            }
        }

        var locations = [];
        var arr = [];
        var folders = [];
        await fetch("http://localhost:8080/api/fileDrop", requestOptions2)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                for (var i in data) {
                    for (var j = 0; j < data[i].length; j++) {
                        if (i.startsWith(username)) {
                            if (i === username) {
                                if (data[i][j].endsWith("txt"))
                                    arr.push(
                                        fileDesc(
                                            data[i][j],
                                            sizes[i][j],
                                            locations[i][j],
                                            "",
                                            data[i][j],
                                            "img",
                                            "img"
                                        )
                                    );
                                else
                                    arr.push({
                                        id: nextId().slice(2, 20),
                                        name: data[i][j],
                                        size: sizes[i][j],
                                        location: "[52.2, 34.3]",
                                        owner: "user1",
                                        address: data[i][j],
                                        type: "img",
                                        mytype: "img",
                                    });
                            } else if (
                                !folders.includes(
                                    i.substr(username.length + 1, i.length - 1)
                                )
                            ) {
                                var lArr = [];
                                var myId = nextId().slice(2, 20);
                                lArr.push({
                                    id: myId,
                                    name: i.substr(
                                        username.length + 1,
                                        i.length - 1
                                    ),
                                    size: 0,
                                    location: "-",
                                    owner: "user1",
                                    address: i.substr(
                                        username.length + 1,
                                        i.length - 1
                                    ),
                                    type: "dir",
                                    mytype: "dir",
                                });
                                for (var k in data) {
                                    for (var l = 0; l < data[k].length; l++) {
                                        var tmp =
                                            username +
                                            "/" +
                                            k.substr(
                                                username.length + 1,
                                                k.length - 1
                                            );
                                        if (k.startsWith(tmp)) {
                                            if (data[k][l].endsWith("txt"))
                                                lArr.push({
                                                    id: nextId().slice(2, 20),
                                                    name: data[k][l],
                                                    size: sizes[k][l],
                                                    location: "[52.2, 34.3]",
                                                    owner: "user1",
                                                    address:
                                                        k.substr(
                                                            username.length + 1,
                                                            k.length - 1
                                                        ) +
                                                        "/" +
                                                        data[k][l],
                                                    type: "text",
                                                    mytype: "text",
                                                });
                                            else
                                                lArr.push({
                                                    id: nextId().slice(2, 20),
                                                    name: data[k][l],
                                                    size: sizes[k][l],
                                                    location: "[52.2, 34.3]",
                                                    owner: "user1",
                                                    address:
                                                        k.substr(
                                                            username.length + 1,
                                                            k.length - 1
                                                        ) +
                                                        "/" +
                                                        data[k][l],
                                                    type: "img",
                                                    mytype: "img",
                                                });
                                        }
                                    }
                                }

                                folders.push(
                                    i.substr(username.length + 1, i.length - 1)
                                );
                                arr.push(lArr);
                            }
                        }
                    }
                }
            });
        setFiles(arr);
        setBackup(arr);
    };

    //
    // On delete account
    //
    const deleteAccount = async () => {
        const requestOptions = {
            method: "GET",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        };
        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/delete",
            requestOptions
        );

        if (rawResponse.status === 200) {
            setRedirect(false);
            setLoggedin(false);

            setFiles([]);
        } else alert("Cannot delete account");
    };

    const onLogOut = async () => {
        const requestOptions = {
            method: "GET",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            credentials: "include",
        };

        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/logOut",
            requestOptions
        );

        if (rawResponse.status === 200) {
            setRedirect(false);
            setLoggedin(false);

            setFiles([]);
        } else alert("Cannot log out");
    };

    const onSignUp = async (username, email, password, dob) => {
        const requestOptions = {
            method: "POST",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username,
                email,
                password,
                dob,
            }),
        };

        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/signUp",
            requestOptions
        );

        if (rawResponse.status === 200) {
            setRedirect(true);
            setLoggedin(true);
        } else alert("Wrong username, email, password, or dob");
    };

    const onFolder = (file2) => {
        console.log("before folder");
        console.log(files);
        setBackup(_.cloneDeep(files));
        setCurrFolder(file2[0].name);
        file2[0].name = "..";
        setFiles(file2);
        console.log("after folder");
        console.log(files);
    };

    const onRestore = () => {
        console.log("before restore");
        console.log(files);
        setFiles(backup);
        console.log("after restore");
        console.log(files);
    };

    const onDelete = async (id) => {
        // Delete from server
        const found = files.find((x) => x.id == id);
        console.log(found.address);
        const options = {
            method: "GET",
            credentials: "include",
        };
        var resp = await fetch(
            `http://localhost:8080/api/fileDrop/delete?file_path=Mariam/${found.address}`,
            options
        );

        // Delete from UI
        // setFiles(files.filter((x) => x.id !== id));
        // setBackup(backup.filter((x) => x.id !== id));
        refresh();
    };

    const onShare = async (id) => {
        const curFile = files.find((element) => element.id === id);

        console.log(curFile);

        setFiles([...files]);
    };

    const onSortAlphaDown = () => {
        setSortBy("name");
        setAscDesc("asc");
        console.log(sortBy, ascDesc);
    };
    const onSortAlphaDownAlt = () => {
        setSortBy("name");
        setAscDesc("desc");
        console.log(sortBy, ascDesc);
    };
    const onSortAmountDown = () => {
        setSortBy("size");
        setAscDesc("asc");
        console.log(sortBy, ascDesc);
    };
    const onSortAmountDownAlt = () => {
        setSortBy("size");
        setAscDesc("desc");
        console.log(sortBy, ascDesc);
    };

    const onOpen = (id) => {
        files.find((file, index, array) => {
            // If we found the file we clicked, and we are in a directory...
            if (array[0].name == ".." && file.id === id)
                setFile(currFolder + "/" + file.name);
            else if (file.id === id) setFile(file.name);
        });
        setId(id);
    };

    const onRename = (value, id) => {
        for (var i in files) {
            if (files[i].id === id) {
                files[i].name = value;
                break; //Stop this loop, we found it!
            }
        }
        setFiles([...files]);
    };

    const onHelp = () => {};

    const onAdd = () => {
        setShowAddFile(!showAddFile);
    };

    const onAddFile = (file) => {
        const data = new FormData();
        data.append("files", file.file);
        data.append("dir", file.dir);

        axios
            .post("http://localhost:8080/api/fileDrop", data, {
                withCredentials: true,
            })
            .then((res) => {
                refresh();
                console.log(res.statusText);
            });
    };
    const onNight = () => {
        document.body.style.backgroundColor === "white"
            ? (document.body.style.backgroundColor = "grey")
            : (document.body.style.backgroundColor = "white");

        document.body.style.color === "black"
            ? (document.body.style.color = "white")
            : (document.body.style.color = "black");
    };

    return (
        <Router>
            <Route
                path="/"
                exact
                render={(props) => (
                    <>
                        <div className="container">
                            <div className="header">
                                {loggedin ? null : (
                                    <div className="toLogin">
                                        <div>
                                            <Link to="/signup">Sign Up</Link>
                                        </div>
                                        <Link to="/login">Login</Link>
                                    </div>
                                )}

                                {loggedin ? (
                                    <div className="loggedIn">
                                        Zalogowano jako {username}
                                        <div>
                                            <button
                                                className="noDeleteButton"
                                                onClick={onLogOut}
                                            >
                                                Log out
                                            </button>
                                            <Popup
                                                open={false}
                                                trigger={
                                                    <button className="deleteButton">
                                                        {" "}
                                                        Usuń Konto
                                                    </button>
                                                }
                                                position="right center"
                                            >
                                                <div>
                                                    Are you sure?
                                                    <button
                                                        className="deleteButton"
                                                        onClick={deleteAccount}
                                                    >
                                                        {" "}
                                                        Yes
                                                    </button>
                                                </div>
                                            </Popup>
                                        </div>
                                    </div>
                                ) : null}

                                <div className="mainImg">
                                    <img
                                        src={logo}
                                        alt="Could not load cloud logo."
                                    />
                                </div>

                                <div className="title unselectable">
                                    File browser
                                </div>
                            </div>
                            <div className="commandBar">
                                <FaPlusSquare
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onAdd}
                                />
                                <FaSortAlphaDown
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onSortAlphaDown}
                                />
                                <FaSortAlphaDownAlt
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onSortAlphaDownAlt}
                                />
                                <FaSortAmountDown
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onSortAmountDown}
                                />
                                <FaSortAmountDownAlt
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onSortAmountDownAlt}
                                />
                                <FaMoon
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onNight}
                                />
                                <Link to="/help">
                                    <FaQuestion
                                        style={{
                                            color: "black",
                                            cursor: "pointer",
                                        }}
                                        onClick={onHelp}
                                    />
                                </Link>
                            </div>
                            {showAddFile && (
                                <AddFile onAddFile={onAddFile} onAdd={onAdd} />
                            )}
                            <div className="topBar">
                                <div> Name</div> <div>Size[KB]</div>{" "}
                                <div>Coordinates</div>
                                <div>Commands</div>
                            </div>
                            <Files
                                files={files}
                                onDelete={onDelete}
                                onShare={onShare}
                                onRename={onRename}
                                sortBy={sortBy}
                                ascDesc={ascDesc}
                                onOpen={onOpen}
                                onFolder={onFolder}
                                onRestore={onRestore}
                            />
                            <div className="footer">
                                Total: {files.length} Files
                            </div>
                        </div>
                    </>
                )}
            />
            <Route
                path="/login"
                component={() => (
                    <>
                        <Login onLogin={onLogin} setRedirect={setRedirect} />
                        {redirect ? <Redirect push to="/"></Redirect> : null}
                    </>
                )}
            />
            <Route
                path="/signup"
                component={() => (
                    <>
                        <SignUp onSignUp={onSignUp} setRedirect={setRedirect} />
                        {redirect ? <Redirect push to="/"></Redirect> : null}
                    </>
                )}
            />
            <Route
                path="/help"
                component={() => (
                    <>
                        <Help onLogin={onLogin} setRedirect={setRedirect} />
                        {/* {redirect ? <Redirect push to="/"></Redirect> : null} */}
                    </>
                )}
            />
            {
                <Route
                    path="/files/:id"
                    component={() => (
                        <Child id={id} file={file} backup={backup}></Child>
                    )}
                ></Route>
            }
        </Router>
    );
};

export default App;
