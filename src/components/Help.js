import { Link } from "react-router-dom";
import logo from "../cloud.png";

const Help = () => {
    return (
        <div className="helpContainer">
            <div className="goBack">
                <Link to="/">Go Back</Link>
            </div>
            <img src={logo} alt="Could not load cloud logo." />
            <div className="title unselectable">Help</div>
            <div>
                Lorem ipsum dolor sit amet consectetur adipisicing elit.
                Deleniti doloribus dolorem fuga natus quaerat voluptas hic
                consectetur, harum dicta labore rerum corrupti officiis corporis
                eveniet nam pariatur? Itaque, repellendus delectus.
            </div>
        </div>
    );
};

export default Help;
