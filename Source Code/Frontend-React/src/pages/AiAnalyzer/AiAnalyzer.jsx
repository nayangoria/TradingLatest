// import React, { useState } from "react";
// import axios from "axios";

// export default function AiAnalyzer() {
//   const [prompt, setPrompt] = useState("");
//   const [response, setResponse] = useState("");
//   const [loading, setLoading] = useState(false);
// const handleSubmit = async () => {
//   if (!prompt.trim()) return;
//   setLoading(true);
//   setResponse("");
//   try {
//     const res = await axios.post(
//       "http://localhost:5454/api/ai/analyze-coin",
//       { prompt: prompt },
//       {
//         headers: {
//           Authorization: `Bearer ${localStorage.getItem('jwt')}`, // or wherever you store your JWT
//           "Content-Type": "application/json",
//         },
//       }
//     );
//     setResponse(res.data.reply || "No response from AI");
//   } catch (err) {
//     console.error(err);
//     setResponse("Error: " + (err.response?.data?.error || err.message));
//   }
//   setLoading(false);
// };


//   return (
//     <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
//       <div className="bg-white shadow-lg rounded-xl max-w-xl w-full p-6">
//         <h1 className="text-2xl font-semibold mb-4 text-gray-800">AI Coin Analyzer</h1>

//         <textarea
//           value={prompt}
//           onChange={(e) => setPrompt(e.target.value)}
//           rows={4}
//           className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 mb-4"
//           placeholder="Enter your query..."
//         />

//         <button
//           onClick={handleSubmit}
//           disabled={loading}
//           className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg transition-colors disabled:opacity-50"
//         >
//           {loading ? "Analyzing..." : "Analyze"}
//         </button>

//         <div className="mt-4 bg-gray-100 rounded-lg p-4 h-64 overflow-y-auto text-gray-800 whitespace-pre-wrap">
//           {loading && <p className="text-gray-500">Loading response...</p>}
//           {!loading && response && <p>{response}</p>}
//         </div>
//       </div>
//     </div>
//   );
// }

// import React, { useState, useRef, useEffect } from "react";
// import axios from "axios";

// const AiAnalyzer=()=> {
//   const [messages, setMessages] = useState([]);
//   const [input, setInput] = useState("");
//   const [loading, setLoading] = useState(false);
//   const chatEndRef = useRef(null);

//   // Scroll chat to bottom
//   useEffect(() => {
//     chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
//   }, [messages]);

//   const sendMessage = async () => {
//     if (!input.trim()) return;

//     const userMessage = { sender: "user", text: input };
//     setMessages((prev) => [...prev, userMessage]);

//     setInput("");
//     setLoading(true);

//     try {
//       const token = localStorage.getItem("jwt");

//       const res = await axios.post(
//         "http://localhost:5454/api/ai/analyze-coin",
//         { prompt: userMessage.text },
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//             "Content-Type": "application/json",
//           },
//         }
//       );

//       const aiReply = res.data.reply || "No response";
//       setMessages((prev) => [...prev, { sender: "ai", text: aiReply }]);
//     } catch (err) {
//       setMessages((prev) => [
//         ...prev,
//         { sender: "ai", text: "⚠️ Error: " + (err.response?.data?.error || err.message) },
//       ]);
//     }

//     setLoading(false);
//   };

//   const handleKeyDown = (e) => {
//     if (e.key === "Enter" && !e.shiftKey) {
//       e.preventDefault();
//       sendMessage();
//     }
//   };

//   return (
//     <div className="flex flex-col items-center w-full min-h-screen bg-gray-100 text-gray-800 p-4">
      
//       {/* Page Title */}
//       <h1 className="text-3xl font-bold text-gray-800 mb-6">AI Trading Assistant</h1>

//       {/* Chat Container */}
//       <div className="w-full max-w-3xl bg-white shadow-lg rounded-xl flex flex-col h-[80vh]">

//         {/* Chat Messages */}
//         <div className="flex-1 p-4 overflow-y-auto space-y-4">
//           {messages.map((msg, index) => (
//             <div
//               key={index}
//               className={`flex ${msg.sender === "user" ? "justify-end" : "justify-start"}`}
//             >
//               <div
//                 className={`px-4 py-2 rounded-xl max-w-[75%] text-sm shadow-sm ${
//                   msg.sender === "user"
//                     ? "bg-blue-500 text-white"
//                     : "bg-gray-200 text-gray-800"
//                 }`}
//               >
//                 {msg.text}
//               </div>
//             </div>
//           ))}

//           {loading && (
//             <div className="flex justify-start">
//               <div className="px-4 py-2 rounded-xl bg-gray-200 text-gray-700 shadow-sm animate-pulse">
//                 Thinking...
//               </div>
//             </div>
//           )}

//           <div ref={chatEndRef} />
//         </div>

//         {/* Input Box */}
//         <div className="p-3 border-t bg-white flex gap-2">
//           <textarea
//             className="flex-1 border rounded-xl p-3 resize-none focus:outline-blue-400 text-sm bg-gray-50"
//             rows="2"
//             placeholder="Ask anything like: Analyze Bitcoin for last 7 days..."
//             value={input}
//             onChange={(e) => setInput(e.target.value)}
//             onKeyDown={handleKeyDown}
//           />

//           <button
//             onClick={sendMessage}
//             disabled={loading}
//             className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-xl text-sm shadow-md disabled:bg-blue-300"
//           >
//             Send
//           </button>
//         </div>
//       </div>
//     </div>
//   );
// }
// export default AiAnalyzer;

import React, { useState } from "react";

export default function AiAnalyzer() {
  const [prompt, setPrompt] = useState("");
  const [response, setResponse] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    if (!prompt.trim()) return;
    setLoading(true);
    setResponse("");
    try {
      const res = await fetch(
        "http://localhost:5454/api/ai/analyze-coin",
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${localStorage.getItem('jwt')}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ message: prompt }) // ✅ FIXED: Changed 'prompt' to 'message'
        }
      );
      const data = await res.json();
      setResponse(data.reply || "No response from AI");
    } catch (err) {
      console.error(err);
      setResponse("Error: " + err.message);
    }
    setLoading(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="bg-white shadow-2xl rounded-2xl max-w-2xl w-full p-8">
        <div className="flex items-center gap-3 mb-6">
          <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center">
            <svg className="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold text-gray-800">AI Coin Analyzer</h1>
        </div>

        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Enter your analysis query
          </label>
          <textarea
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            rows={4}
            className="w-full p-4 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all text-gray-800 placeholder-gray-400"
            placeholder="Example: Analyze Solana for the last 10 days"
          />
          <p className="mt-2 text-sm text-gray-500">
            Try: "Ethereum 7 days", "Solana 30 days analysis", "Dogecoin 14 days"
          </p>
        </div>

        <button
          onClick={handleSubmit}
          disabled={loading}
          className="w-full bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white font-semibold py-3 px-6 rounded-xl transition-all transform hover:scale-[1.02] disabled:opacity-50 disabled:cursor-not-allowed shadow-lg"
        >
          {loading ? (
            <span className="flex items-center justify-center gap-2">
              <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
              </svg>
              Analyzing...
            </span>
          ) : (
            "Analyze"
          )}
        </button>

        <div className="mt-6">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-sm font-semibold text-gray-700 uppercase tracking-wide">Analysis Result</h2>
            {response && !loading && (
              <button
                onClick={() => {
                  navigator.clipboard.writeText(response);
                }}
                className="text-xs text-blue-600 hover:text-blue-700 font-medium"
              >
                Copy
              </button>
            )}
          </div>
          <div className="bg-gray-50 border-2 border-gray-200 rounded-xl p-5 min-h-[300px] max-h-[500px] overflow-y-auto">
            {loading && (
              <div className="flex flex-col items-center justify-center h-full text-gray-500">
                <svg className="animate-spin h-10 w-10 mb-3 text-blue-500" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                </svg>
                <p className="font-medium">Analyzing cryptocurrency data...</p>
              </div>
            )}
            {!loading && response && (
              <pre className="text-gray-800 whitespace-pre-wrap font-mono text-sm leading-relaxed">
                {response}
              </pre>
            )}
            {!loading && !response && (
              <div className="flex flex-col items-center justify-center h-full text-gray-400">
                <svg className="w-16 h-16 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
                <p className="font-medium">No analysis yet</p>
                <p className="text-sm mt-1">Enter a query and click Analyze</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}