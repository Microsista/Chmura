// Custom components
import Login from "./components/Login";
import SignUp from "./components/SignUp";
import Files from "./components/Files";
import AddFile from "./components/AddFile";
import Help from "./components/Help";
import Child from "./components/Child";

// Resources
import logo from "./cloud.png";

// External libraries
import { useState } from "react";
import { BrowserRouter as Router, Link, Route, Redirect } from "react-router-dom";
import { FaSortAmountDown, FaSortAmountDownAlt, FaSortAlphaDown,
    FaSortAlphaDownAlt, FaQuestion, FaPlusSquare, FaMoon } from "react-icons/fa";
import nextId from "react-id-generator";
import Popup from "reactjs-popup";
import axios from "axios";
import * as _ from "lodash";
import { useEffect } from "react";

const App = () => {
    //
    // Callbacks
    //

    const onGoBack = () => {
        setDummy(dummy+1);
    }

    const onLogin = async (username, password) => {
        const requestOptions = {
            method: "POST",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
                //Authorization: 
            },
            body: JSON.stringify({
                username,
                password,
            }),
            //credentials: "include",
            //mode: "no-cors"
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

            // Inlined refresh function for reliability
            const options = {
                method: "GET",
                credentials: "include",
            };
    
            // Initial fetch of the data to iterate over and get file sizes.
            var myData;
            await fetch("http://localhost:8080/api/fileDrop", options).then((response) => response.json()).then((data) => {
                myData = data;
            });
    
            var k = 0;
            console.log()
            for (var i in myData) k++;
            // Iterate over the data and retrieve file sizes.
            var sizes = new Array(k);
            for (var i in myData)
                sizes[i] = new Array(myData[i].length);
            for (var i in myData) {
                for (var j = 0; j < myData[i].length; j++) {
                    var resp = await fetch(
                        `http://localhost:8080/api/fileDrop/download?file_path=${username}/${myData[i][j]}`,
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
                    var folderName = i.substr(username.length + 1, i.length - 1);
                    if (i === username) // if this is main directory
                        for (var j = 0; j < data[i].length; j++)
                            checkSubfolder(arr, data[i][j], sizes[i][j], "", data[i][j]);
                    else { // if this is a subdirectory
                        var lArr = [];
                        if (!folders.includes(folderName))
                            lArr.push(fileDesc(folderName, 0, "", "", folderName, "dir", "dir"));
                        for (var j = 0; j < data[i].length; j++) {
                            var folderLocation = username + "/" + folderName;
                            if (i.startsWith(folderLocation))
                                checkSubfolder(lArr, data[i][j], sizes[i][j], "", folderName + "/" + data[i][j]);
                            folders.push(folderLocation);
                        }
                        arr.push(lArr);
                    }  
                }
            });
               
            setFiles(arr);
            setBackup(arr);
        } else alert("Wrong username or password");
    };

    const deleteAccount = async () => {
        const requestOptions = {
            method: "GET",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
                credentials: "include",
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
            setUsername(username);

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
    
                // Inlined refresh function for reliability
                const options = {
                    method: "GET",
                    credentials: "include",
                };
        
                // Initial fetch of the data to iterate over and get file sizes.
                var myData;
                await fetch("http://localhost:8080/api/fileDrop", options).then((response) => response.json()).then((data) => {
                    myData = data;
                });
        
                var k = 0;
                console.log()
                for (var i in myData) k++;
                // Iterate over the data and retrieve file sizes.
                var sizes = new Array(k);
                for (var i in myData)
                    sizes[i] = new Array(myData[i].length);
                for (var i in myData) {
                    for (var j = 0; j < myData[i].length; j++) {
                        var resp = await fetch(
                            `http://localhost:8080/api/fileDrop/download?file_path=${username}/${myData[i][j]}`,
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
                        var folderName = i.substr(username.length + 1, i.length - 1);
                        if (i === username) // if this is main directory
                            for (var j = 0; j < data[i].length; j++)
                                checkSubfolder(arr, data[i][j], sizes[i][j], "", data[i][j]);
                        else { // if this is a subdirectory
                            var lArr = [];
                            if (!folders.includes(folderName))
                                lArr.push(fileDesc(folderName, 0, "", "", folderName, "dir", "dir"));
                            for (var j = 0; j < data[i].length; j++) {
                                var folderLocation = username + "/" + folderName;
                                if (i.startsWith(folderLocation))
                                    checkSubfolder(lArr, data[i][j], sizes[i][j], "", folderName + "/" + data[i][j]);
                                folders.push(folderLocation);
                            }
                            arr.push(lArr);
                        }  
                    }
                });
                   
                setFiles(arr);
                setBackup(arr);
            } else alert("Wrong username or password");



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
            `http://localhost:8080/api/fileDrop/delete?file_path=${username}/${found.address}`,
            options
        );

        // Delete from UI
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
            ? (document.body.style.backgroundColor = "black")
            : (document.body.style.backgroundColor = "white");

        document.body.style.color === "black"
            ? (document.body.style.color = "white")
            : (document.body.style.color = "black");

        var elements = document.getElementsByClassName("fa");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black" ? elements.item(i).style.color = "white" : elements.item(i).style.color = "black";
        }

        var elements = document.getElementsByClassName("logo");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black" ? elements.item(i).style.filter="invert(100%)" : elements.item(i).style.filter="invert(0%)";
        }
    };

    //
    // Helper functions
    //

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

    const refresh = async () => {
        

        const options = {
            method: "GET",
            credentials: "include",
        };

        // Initial fetch of the data to iterate over and get file sizes.
        var myData;
        await fetch("http://localhost:8080/api/fileDrop", options).then((response) => response.json()).then((data) => {
            myData = data;
        });

        var k = 0;
        console.log()
        for (var i in myData) k++;
        // Iterate over the data and retrieve file sizes.
        var sizes = new Array(k);
        for (var i in myData)
            sizes[i] = new Array(myData[i].length);
        for (var i in myData) {
            for (var j = 0; j < myData[i].length; j++) {
                var resp = await fetch(
                    `http://localhost:8080/api/fileDrop/download?file_path=${username}/${myData[i][j]}`,
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
                var folderName = i.substr(username.length + 1, i.length - 1);
                if (i === username) // if this is main directory
                    for (var j = 0; j < data[i].length; j++)
                        checkSubfolder(arr, data[i][j], sizes[i][j], "", data[i][j]);
                else { // if this is a subdirectory
                    var lArr = [];
                    if (!folders.includes(folderName))
                        lArr.push(fileDesc(folderName, 0, "", "", folderName, "dir", "dir"));
                    for (var j = 0; j < data[i].length; j++) {
                        var folderLocation = username + "/" + folderName;
                        if (i.startsWith(folderLocation))
                            checkSubfolder(lArr, data[i][j], sizes[i][j], "", folderName + "/" + data[i][j]);
                        folders.push(folderLocation);
                    }
                    arr.push(lArr);
                }  
            }
        });
           
        setFiles(arr);
        setBackup(arr);
    };

    //
    // State
    //

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
    const [dummy, setDummy] = useState(0);

    useEffect(() => {
        var elements = document.getElementsByClassName("fa");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black" ? elements.item(i).style.color = "white" : elements.item(i).style.color = "black";
        }

        var elements = document.getElementsByClassName("logo");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black" ? elements.item(i).style.filter="invert(100%)" : elements.item(i).style.filter="invert(0%)";
        }
    })

    //
    // Render
    //

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
                                        className="logo"
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
                                    className="fa"
                                    onClick={onAdd}
                                />
                                <FaSortAlphaDown
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onSortAlphaDown}
                                />
                                <FaSortAlphaDownAlt
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onSortAlphaDownAlt}
                                />
                                <FaSortAmountDown
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onSortAmountDown}
                                />
                                <FaSortAmountDownAlt
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onSortAmountDownAlt}
                                />
                                <FaMoon
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onNight}
                                />
                                <Link to="/help">
                                    <FaQuestion
                                        style={{
                                            color: "black",
                                            cursor: "pointer",
                                        }}
                                        className="fa"
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
                                Total: {files.length} Paths
                            </div>
                        </div>
                    </>
                )}
            />
            <Route
                path="/login"
                component={() => (
                    <>
                        <Login onLogin={onLogin} onGoBack={onGoBack} setRedirect={setRedirect} />
                        {redirect ? <Redirect push to="/"></Redirect> : null}
                    </>
                )}
            />
            <Route
                path="/signup"
                component={() => (
                    <>
                        <SignUp onSignUp={onSignUp} setRedirect={setRedirect} onGoBack={onGoBack}/>
                        {redirect ? <Redirect push to="/"></Redirect> : null}
                    </>
                )}
            />
            <Route
                path="/help"
                component={() => (
                    <>
                        <Help onLogin={onLogin} setRedirect={setRedirect} onGoBack={onGoBack}/>
                        {/* {redirect ? <Redirect push to="/"></Redirect> : null} */}
                    </>
                )}
            />
            {
                <Route
                    path="/files/:id"
                    component={() => (
                        <Child id={id} file={file} backup={backup} username={username} onGoBack={onGoBack}></Child>
                    )}
                ></Route>
            }
        </Router>
    );
};

export default App;
