import { Link } from "react-router-dom";
import { useState, useEffect } from "react";
import logo from "../cloud.png";
// import dp from "react-datepicker";
import DatePicker from "react-datepicker";

const SignUp = ({ onSignUp, onGoBack }) => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [dob, setDob] = useState(new Date());

    const onSubmit = (e) => {
        e.preventDefault();

        // if (username !== "john") {
        //     alert("You're not john");
        //     return;
        // }

        onSignUp(username, email, password, dob);
    };

    const onBack = () => {
        onGoBack()
    }

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

    return (
        <form className="login-form" onSubmit={onSubmit}>
            <div className="loginContainer">
                <div className="goBack">
                    <Link to="/" onClick={onBack}>Go Back</Link>
                </div>
                <img className="logo" src={logo} alt="Could not load cloud logo." />
                <div className="title unselectable">Sign up</div>

                <div className="form-control">
                    <label className="unselectable">Username (at least 3 characters)</label>
                    <input
                        type="text"
                        placeholder="Enter your username"
                        // value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>

                <div className="form-control">
                    <label className="unselectable">Email</label>
                    <input
                        type="text"
                        placeholder="Enter your email"
                        // value={username}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>

                <div className="form-control">
                    <label className="unselectable">Password (at least 6 characters)</label>
                    <input
                        type="password"
                        placeholder="Enter your password"
                        // value={username}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <div>
                    <label className="unselectable">Date of Birth</label>
                    <div>
                        <DatePicker
                            selected={dob}
                            onChange={(date) => setDob(date)}
                        />
                    </div>
                </div>
                <input
                    type="submit"
                    value="Sign up"
                    className="btn btn-block"
                    readOnly
                />
            </div>
        </form>
    );
};

export default SignUp;
