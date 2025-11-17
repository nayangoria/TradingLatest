// /* eslint-disable no-unused-vars */
// import "./Auth.css";
// import { Button } from "@/components/ui/button";

// import SignupForm from "./signup/SignupForm";
// import LoginForm from "./login/login";
// import { useLocation, useNavigate } from "react-router-dom";
// import { useState } from "react";
// import { Card } from "@/components/ui/card";
// import ForgotPassword from "./ForgotPassword";
// import ForgotPasswordForm from "./ForgotPassword";
// import { Skeleton } from "@/components/ui/skeleton";
// import { useSelector } from "react-redux";
// import SpinnerBackdrop from "@/components/custome/SpinnerBackdrop";
// import { Avatar, AvatarImage } from "@/components/ui/avatar";
// import { AvatarFallback } from "@radix-ui/react-avatar";
// import { ToastAction } from "@/components/ui/toast";
// import { useToast } from "@/components/ui/use-toast";
// import CustomeToast from "@/components/custome/CustomeToast";

// const Auth = () => {
//   const navigate = useNavigate();
//   const location = useLocation();
//   const { auth } = useSelector((store) => store);
//   const { toast } = useToast();

//   const [animate, setAnimate] = useState(false);

//   const handleNavigation = (path) => {
//     // setAnimate(true);
//     // setTimeout(() => {
//     navigate(path);
//     //   setAnimate(false);
//     // }, 500);
//     // Adjust the delay as needed to match your animation duration
//     // setAnimate(false)
//   };

//   const [showToast, setShowToast] = useState(false);

//   const handleShowToast = () => {
//     setShowToast(true);
//   };

// console.log("---------- ",auth.error)


//   return (
//     <div className={`authContainer h-screen relative`}>
//       <div className="absolute top-0 right-0 left-0 bottom-0 bg-[#030712] bg-opacity-50"></div>

//       <div
//         className={`bgBlure absolute top-1/2 left-1/2 transform -translate-x-1/2  -translate-y-1/2 box flex flex-col justify-center items-center  h-[35rem] w-[30rem]  rounded-md z-50 bg-black bg-opacity-50 shadow-2xl shadow-white`}
//       >
//          <CustomeToast show={auth.error} message={auth.error?.error}/>
     

//         <h1 className="text-6xl font-bold pb-9">Zosh Trading</h1>
//         {/* <Avatar>
//           <AvatarImage src="https://cdn.pixabay.com/photo/2019/04/15/20/42/bitcoin-4130299_1280.png"/>
//           <AvatarFallback>BTC</AvatarFallback>
//         </Avatar> */}

//         {location.pathname == "/signup" ? (
//           <section
//             className={`w-full login  ${animate ? "slide-down" : "slide-up"}`}
//           >
//             <div className={`  loginBox  w-full px-10 space-y-5 `}>
//               <SignupForm />

//               {location.pathname == "/signup" ? (
//                 <div className="flex items-center justify-center">
//                   <span> {"don't have account ?"} </span>
//                   <Button
//                     onClick={() => handleNavigation("/signin")}
//                     variant="ghost"
//                   >
//                     signin
//                   </Button>
//                 </div>
//               ) : (
//                 <div className="flex items-center justify-center">
//                   <span>already have account ? </span>
//                   <Button
//                     onClick={() => handleNavigation("/signup")}
//                     variant="ghost"
//                   >
//                     signup
//                   </Button>
//                 </div>
//               )}
//             </div>
//           </section>
//         ) : location.pathname == "/forgot-password" ? (
//           <section className="p-5 w-full">
//             <ForgotPasswordForm />
//             {/* <Button variant="outline" className="w-full py-5 mt-5">
//               Try Using Mobile Number
//             </Button> */}
//             <div className="flex items-center justify-center mt-5">
//               <span>Back To Login ? </span>
//               <Button onClick={() => navigate("/signin")} variant="ghost">
//                 signin
//               </Button>
//             </div>
//           </section>
//         ) : (
//           <>
//             {
//               <section className={`w-full login`}>
//                 <div className={`  loginBox  w-full px-10 space-y-5 `}>
//                   <LoginForm />

//                   <div className="flex items-center justify-center">
//                     <span>already have account ? </span>
//                     <Button
//                       onClick={() => handleNavigation("/signup")}
//                       variant="ghost"
//                     >
//                       signup
//                     </Button>
//                   </div>
//                   <div className="">
//                     <Button
//                       onClick={() => navigate("/forgot-password")}
//                       variant="outline"
//                       className="w-full py-5"
//                     >
//                       Forgot Password ?
//                     </Button>
//                   </div>
//                 </div>
//               </section>
//             }
//           </>
//         )}


//       </div>
      
    

//     </div>
//   );
// };

// export default Auth;


"use client"

/* eslint-disable no-unused-vars */
// import "./Auth.css";
import { Button } from "@/components/ui/button"

import SignupForm from "./signup/SignupForm"
import LoginForm from "./login/login"
import { useLocation, useNavigate } from "react-router-dom"
import { useState } from "react"
import ForgotPasswordForm from "./ForgotPassword"
import { useSelector } from "react-redux"
import { useToast } from "@/components/ui/use-toast"
import CustomeToast from "@/components/custome/CustomeToast"

const Auth = () => {
    const navigate = useNavigate()
    const location = useLocation()
    const { auth } = useSelector((store) => store)
    const { toast } = useToast()

    const [animate, setAnimate] = useState(false)

    const handleNavigation = (path) => {
        // setAnimate(true);
        // setTimeout(() => {
        navigate(path)
        //   setAnimate(false);
        // }, 500);
        // Adjust the delay as needed to match your animation duration
        // setAnimate(false)
    }

    const [showToast, setShowToast] = useState(false)

    const handleShowToast = () => {
        setShowToast(true)
    }

    console.log("---------- ", auth.error)

    return (
        <div className={`authContainer h-screen relative overflow-hidden bg-[#f8f9fa]`}>
            <div className="absolute top-0 left-0 z-0 w-full h-full">
                <svg
                    className="absolute top-0 left-0 w-full h-full"
                    viewBox="0 0 1440 969"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                    preserveAspectRatio="xMidYMid slice"
                >
                    <mask
                        id="mask0_95:1005"
                        style={{ maskType: "alpha" }}
                        maskUnits="userSpaceOnUse"
                        x="0"
                        y="0"
                        width="1440"
                        height="969"
                    >
                        <rect width="1440" height="969" fill="#f8f9fa" />
                    </mask>
                    <g mask="url(#mask0_95:1005)">
                        <path
                            opacity="0.08"
                            d="M1086.96 297.978L632.959 554.978L935.625 535.926L1086.96 297.978Z"
                            fill="url(#paint0_linear_95:1005)"
                        />
                        <path
                            opacity="0.08"
                            d="M1324.5 755.5L1450 687V886.5L1324.5 967.5L-10 288L1324.5 755.5Z"
                            fill="url(#paint1_linear_95:1005)"
                        />
                    </g>
                    <defs>
                        <linearGradient
                            id="paint0_linear_95:1005"
                            x1="1178.4"
                            y1="151.853"
                            x2="780.959"
                            y2="453.581"
                            gradientUnits="userSpaceOnUse"
                        >
                            <stop stopColor="#4A6CF7" />
                            <stop offset="1" stopColor="#4A6CF7" stopOpacity="0" />
                        </linearGradient>
                        <linearGradient
                            id="paint1_linear_95:1005"
                            x1="160.5"
                            y1="220"
                            x2="1099.45"
                            y2="1192.04"
                            gradientUnits="userSpaceOnUse"
                        >
                            <stop stopColor="#4A6CF7" />
                            <stop offset="1" stopColor="#4A6CF7" stopOpacity="0" />
                        </linearGradient>
                    </defs>
                </svg>
            </div>

            <div
                className={`bgBlure absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 box flex flex-col justify-center items-center h-auto min-h-[35rem] w-full max-w-[500px] mx-4 rounded-lg z-50 bg-white shadow-[0_0_40px_rgba(74,108,247,0.1)] p-10 transition-all duration-300 hover:shadow-[0_0_50px_rgba(74,108,247,0.15)]`}
            >
                <CustomeToast show={auth.error} message={auth.error?.error} />

                <h1 className="text-3xl sm:text-4xl font-bold pb-9 text-gray-900">Demo Crypto Trading</h1>

                {location.pathname == "/signup" ? (
                    <section className={`w-full login  ${animate ? "slide-down" : "slide-up"}`}>
                        <div className={`loginBox w-full space-y-5`}>
                            <SignupForm />

                            {location.pathname == "/signup" ? (
                                <div className="flex items-center justify-center">
                                    <span className="text-gray-600 text-sm"> {"don't have account ?"} </span>
                                    <Button
                                        onClick={() => handleNavigation("/signin")}
                                        variant="ghost"
                                        className="text-[#4A6CF7] hover:text-[#3d5dd6] hover:bg-[#4A6CF7]/5 transition-all duration-300"
                                    >
                                        signin
                                    </Button>
                                </div>
                            ) : (
                                <div className="flex items-center justify-center">
                                    <span className="text-gray-600 text-sm">already have account ? </span>
                                    <Button
                                        onClick={() => handleNavigation("/signup")}
                                        variant="ghost"
                                        className="text-[#4A6CF7] hover:text-[#3d5dd6] hover:bg-[#4A6CF7]/5 transition-all duration-300"
                                    >
                                        signup
                                    </Button>
                                </div>
                            )}
                        </div>
                    </section>
                ) : location.pathname == "/forgot-password" ? (
                    <section className="w-full">
                        <ForgotPasswordForm />
                        <div className="flex items-center justify-center mt-5">
                            <span className="text-gray-600 text-sm">Back To Login ? </span>
                            <Button
                                onClick={() => navigate("/signin")}
                                variant="ghost"
                                className="text-[#4A6CF7] hover:text-[#3d5dd6] hover:bg-[#4A6CF7]/5 transition-all duration-300"
                            >
                                signin
                            </Button>
                        </div>
                    </section>
                ) : (
                    <>
                        <section className={`w-full login`}>
                            <div className={`loginBox w-full space-y-5`}>
                                <LoginForm />

                                <div className="flex items-center justify-center">
                                    <span className="text-gray-600 text-sm">already have account ? </span>
                                    <Button
                                        onClick={() => handleNavigation("/signup")}
                                        variant="ghost"
                                        className="text-[#4A6CF7] hover:text-[#3d5dd6] hover:bg-[#4A6CF7]/5 transition-all duration-300"
                                    >
                                        signup
                                    </Button>
                                </div>
                                <div className="">
                                    <Button
                                        onClick={() => navigate("/forgot-password")}
                                        variant="outline"
                                        className="w-full py-5 border border-gray-200 text-gray-700 bg-[#f8f8f8] hover:border-[#4A6CF7] hover:bg-[#4A6CF7]/5 hover:text-[#4A6CF7] transition-all duration-300"
                                    >
                                        Forgot Password ?
                                    </Button>
                                </div>
                            </div>
                        </section>
                    </>
                )}
            </div>
        </div>
    )
}

export default Auth
