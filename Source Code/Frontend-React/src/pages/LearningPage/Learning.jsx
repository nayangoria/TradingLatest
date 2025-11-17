
import React from 'react';
import { PlayCircle, Clock } from 'lucide-react';

const Learning = () => {
  const videos = [
    {
      id: 1,
      url: '/public/how to use platform.mp4',
      title: 'How to Use a Demo Crypto Trading Application',
      description: 'A step-by-step walkthrough explaining how to navigate a demo crypto trading platform, view market data, place mock trades, and understand the interface.',
      duration: '1:23 min',
      isLocal: true
    },
    {
      id: 2,
      url: '/public/how to add money.mp4',
      title: 'How to Add Money to Your Crypto Wallet (Beginner Guide)',
      description: 'A beginner-friendly guide explaining methods to deposit funds into your crypto wallet, including payment gateways, bank transfers, and UPI processes.',
      duration: '1:24 min',
      isLocal: true
    },
    {
      id: 3,
      url: '/public/how to buy and sell.mp4',
      title: 'How to Buy and Sell Cryptocurrency (Step-by-Step)',
      description: 'Learn how to execute buy and sell orders, understand order types, check fees, read candlesticks, and manage your transactions on a trading platform.',
      duration: '1:26 min',
      isLocal: false
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-5xl mx-auto px-6 py-5">
          <h1 className="text-3xl font-bold text-gray-800">Learning Path</h1>
        </div>
      </header>

      {/* Learning Path Timeline */}
      <main className="max-w-5xl mx-auto px-6 py-10">
        <div className="relative">
          {/* Central Thread Line */}
          <div className="absolute left-1/2 transform -translate-x-1/2 top-0 bottom-0 w-0.5 bg-blue-300 hidden md:block" />

          {/* Video Cards */}
          <div className="space-y-12">
            {videos.map((video, index) => {
              const isRight = index % 2 === 0;
              
              return (
                <div key={video.id} className="relative">
                  {/* Timeline Dot */}
                  <div className="absolute left-1/2 transform -translate-x-1/2 w-4 h-4 bg-blue-500 rounded-full border-4 border-white shadow-md z-10 hidden md:block" />

                  {/* Card Container */}
                  <div className={`flex flex-col md:flex-row gap-6 items-center ${isRight ? 'md:flex-row-reverse' : ''}`}>
                    {/* Video Section */}
                    <div className="md:w-1/2 flex justify-end">
                      <div className="w-full max-w-sm">
                        <div className="bg-white rounded-xl border-4 border-dashed border-blue-400 p-3 shadow-lg">
                          <div className="aspect-video bg-gray-800 rounded-lg relative overflow-hidden">
                            {video.isLocal ? (
                              <video
                                src={video.url}
                                controls
                                className="w-full h-full object-cover"
                              >
                                Your browser does not support the video tag.
                              </video>
                            ) : (
                              <iframe
                                src={video.url}
                                title={video.title}
                                className="w-full h-full"
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                allowFullScreen
                              />
                            )}
                          </div>
                        </div>
                      </div>
                    </div>

                    {/* Description Section */}
                    <div className="md:w-1/2">
                      <div className="bg-white rounded-xl p-6 shadow-md border border-gray-200 max-w-sm">
                        <h3 className="text-2xl font-bold text-gray-800 mb-3">
                          {video.title}
                        </h3>
                        
                        <p className="text-gray-600 leading-relaxed text-sm mb-4">
                          {video.description}
                        </p>

                        <div className="flex items-center justify-between">
                          <span className="text-sm text-gray-500 flex items-center gap-1">
                            <Clock size={14} />
                            {video.duration}
                          </span>
                          <button className="bg-blue-500 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-blue-600 transition-colors flex items-center gap-2">
                            <PlayCircle size={16} />
                            Watch
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </main>
    </div>
  );
}
export default Learning;

// import  { useEffect, useMemo } from 'react';
// import { useDispatch, useSelector } from 'react-redux';
// import { PlayCircle, Clock, CheckCircle, Loader } from 'lucide-react';
// import { 
//   getVideosWithProgress, 
//   markVideoAsComplete, 
//   updateVideoWatch 
// } from '@/Redux/LearningPath/LearningPathAction';

// const Learning = () => {
//   const dispatch = useDispatch();
  
//   // Optimized selectors - select only what you need
//   const videos = useSelector((state) => state.learningPath?.videos || []);
//   const loading = useSelector((state) => state.learningPath?.loading || false);
//   const error = useSelector((state) => state.learningPath?.error || null);
//   const markingComplete = useSelector((state) => state.learningPath?.markingComplete || false);
//   const jwt = useSelector((state) => state.auth?.jwt || null);

//   useEffect(() => {
//     const token = jwt || localStorage.getItem('token');
//     if (token) {
//       dispatch(getVideosWithProgress(token));
//     }
//   }, [dispatch, jwt]);

//   const handleMarkAsComplete = (videoId) => {
//     const token = jwt || localStorage.getItem('token');
//     if (token) {
//       dispatch(markVideoAsComplete({ videoId, jwt: token }));
//     }
//   };

//   const handleUpdateLastWatched = (videoId) => {
//     const token = jwt || localStorage.getItem('token');
//     if (token) {
//       dispatch(updateVideoWatch({ videoId, jwt: token }));
//     }
//   };

//   // Memoize calculated progress
//   const progressPercentage = useMemo(() => {
//     if (!videos || videos.length === 0) return 0;
//     const completed = videos.filter(v => v.completed).length;
//     return Math.round((completed / videos.length) * 100);
//   }, [videos]);

//   // Memoize completed count
//   const completedCount = useMemo(() => {
//     return videos.filter(v => v.completed).length;
//   }, [videos]);

//   if (loading) {
//     return (
//       <div className="min-h-screen bg-gray-50 flex items-center justify-center">
//         <div className="text-center">
//           <Loader className="animate-spin text-blue-500 mx-auto mb-4" size={48} />
//           <p className="text-gray-600">Loading your learning path...</p>
//         </div>
//       </div>
//     );
//   }

//   return (
//     <div className="min-h-screen bg-gray-50">
//       {/* Header */}
//       <header className="bg-white shadow-sm border-b">
//         <div className="max-w-5xl mx-auto px-6 py-5">
//           <div className="flex justify-between items-center">
//             <div>
//               <h1 className="text-3xl font-bold text-gray-800">Learning Path</h1>
//               <p className="text-sm text-gray-600 mt-1">Track your progress as you learn</p>
//             </div>
//             <div className="text-right">
//               <div className="text-2xl font-bold text-blue-600">{progressPercentage}%</div>
//               <div className="text-xs text-gray-600">Complete</div>
//             </div>
//           </div>
          
//           {/* Progress Bar */}
//           <div className="mt-4 bg-gray-200 rounded-full h-2 overflow-hidden">
//             <div 
//               className="bg-gradient-to-r from-blue-500 to-green-500 h-full transition-all duration-500"
//               style={{ width: `${progressPercentage}%` }}
//             />
//           </div>
//         </div>
//       </header>

//       {/* Error Message */}
//       {error && (
//         <div className="max-w-5xl mx-auto px-6 mt-6">
//           <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-800">
//             <p className="text-sm">⚠️ Error: {error}</p>
//           </div>
//         </div>
//       )}

//       {/* No Videos Message */}
//       {!loading && videos.length === 0 && (
//         <div className="max-w-5xl mx-auto px-6 mt-20">
//           <div className="text-center bg-white rounded-xl p-12 shadow-md">
//             <PlayCircle className="mx-auto mb-4 text-gray-400" size={64} />
//             <h3 className="text-2xl font-bold text-gray-800 mb-2">No Videos Available</h3>
//             <p className="text-gray-600">Check back later for learning content!</p>
//           </div>
//         </div>
//       )}

//       {/* Learning Path Timeline */}
//       {videos.length > 0 && (
//         <main className="max-w-5xl mx-auto px-6 py-10">
//           <div className="relative">
//             {/* Central Thread Line */}
//             <div className="absolute left-1/2 transform -translate-x-1/2 top-0 bottom-0 w-0.5 bg-blue-300 hidden md:block" />

//             {/* Video Cards */}
//             <div className="space-y-12">
//               {videos.map((video, index) => {
//                 const isRight = index % 2 === 0;
                
//                 return (
//                   <div key={video.id} className="relative">
//                     {/* Timeline Dot */}
//                     <div className={`absolute left-1/2 transform -translate-x-1/2 w-4 h-4 rounded-full border-4 border-white shadow-md z-10 hidden md:block ${
//                       video.completed ? 'bg-green-500' : 'bg-blue-500'
//                     }`} />

//                     {/* Card Container */}
//                     <div className={`flex flex-col md:flex-row gap-6 items-center ${isRight ? 'md:flex-row-reverse' : ''}`}>
//                       {/* Video Section */}
//                       <div className="md:w-1/2 flex justify-end">
//                         <div className="w-full max-w-sm relative">
//                           {video.completed && (
//                             <div className="absolute -top-3 -right-3 bg-green-500 text-white rounded-full p-2 shadow-lg z-20">
//                               <CheckCircle size={24} />
//                             </div>
//                           )}
//                           <div className={`bg-white rounded-xl border-4 border-dashed p-3 shadow-lg ${
//                             video.completed ? 'border-green-400' : 'border-blue-400'
//                           }`}>
//                             <div 
//                               className="aspect-video bg-gray-800 rounded-lg relative overflow-hidden cursor-pointer"
//                               onClick={() => handleUpdateLastWatched(video.id)}
//                             >
//                               <iframe
//                                 src={video.videoUrl}
//                                 title={video.title}
//                                 className="w-full h-full"
//                                 allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
//                                 allowFullScreen
//                               />
//                             </div>
//                           </div>
//                         </div>
//                       </div>

//                       {/* Description Section */}
//                       <div className="md:w-1/2">
//                         <div className={`bg-white rounded-xl p-6 shadow-md border max-w-sm ${
//                           video.completed ? 'border-green-200 bg-green-50' : 'border-gray-200'
//                         }`}>
//                           <div className="flex items-start justify-between mb-3">
//                             <h3 className="text-2xl font-bold text-gray-800">
//                               {video.title}
//                             </h3>
//                             {video.completed && (
//                               <span className="text-xs bg-green-500 text-white px-2 py-1 rounded-full">
//                                 ✓ Done
//                               </span>
//                             )}
//                           </div>
                          
//                           <p className="text-gray-600 leading-relaxed text-sm mb-4">
//                             {video.description}
//                           </p>

//                           <div className="flex items-center justify-between">
//                             <span className="text-sm text-gray-500 flex items-center gap-1">
//                               <Clock size={14} />
//                               {video.duration}
//                             </span>
//                             {!video.completed ? (
//                               <button 
//                                 onClick={() => handleMarkAsComplete(video.id)}
//                                 disabled={markingComplete}
//                                 className="bg-blue-500 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-blue-600 transition-colors flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
//                               >
//                                 {markingComplete ? (
//                                   <Loader className="animate-spin" size={16} />
//                                 ) : (
//                                   <CheckCircle size={16} />
//                                 )}
//                                 Mark Complete
//                               </button>
//                             ) : (
//                               <button 
//                                 className="bg-green-500 text-white px-4 py-2 rounded-lg text-sm font-semibold cursor-default flex items-center gap-2"
//                               >
//                                 <CheckCircle size={16} />
//                                 Completed
//                               </button>
//                             )}
//                           </div>
//                         </div>
//                       </div>
//                     </div>
//                   </div>
//                 );
//               })}
//             </div>
//           </div>
//         </main>
//       )}

//       {/* Summary Footer */}
//       {videos.length > 0 && (
//         <footer className="mt-16 bg-white border-t py-8">
//           <div className="max-w-5xl mx-auto px-6 text-center">
//             <p className="text-gray-600">
//               <span className="font-semibold text-blue-600">{completedCount}</span> of{' '}
//               <span className="font-semibold text-blue-600">{videos.length}</span> lessons completed
//             </p>
//           </div>
//         </footer>
//       )}
//     </div>
//   );
// };

// export default Learning;


// import React from 'react';
// import { PlayCircle, Clock } from 'lucide-react';

// const Learning=()=> {
//   const videos = [
//     {
//       id: 1,
//       url: 'https://www.youtube.com/embed/dQw4w9WgXcQ',
//       title: 'Lesson 1',
//       description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
//       duration: '15 min'
//     },
//     {
//       id: 2,
//       url: 'https://www.youtube.com/embed/BE9xgob3iTQ',
//       title: 'Lesson 2',
//       description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
//       duration: '20 min'
//     },
//     {
//       id: 3,
//       url: 'https://www.youtube.com/embed/9bZkp7q19f0',
//       title: 'Lesson 3',
//       description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
//       duration: '25 min'
//     },
//     {
//       id: 4,
//       url: 'https://www.youtube.com/embed/3JZ_D3ELwOQ',
//       title: 'Lesson 4',
//       description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
//       duration: '30 min'
//     }
//   ];

//   return (
//     <div className="min-h-screen bg-gray-50">
//       {/* Header */}
//       <header className="bg-white shadow-sm border-b">
//         <div className="max-w-5xl mx-auto px-6 py-5">
//           <h1 className="text-3xl font-bold text-gray-800">Learning Path</h1>
//         </div>
//       </header>

//       {/* Learning Path Timeline */}
//       <main className="max-w-5xl mx-auto px-6 py-10">
//         <div className="relative">
//           {/* Central Thread Line */}
//           <div className="absolute left-1/2 transform -translate-x-1/2 top-0 bottom-0 w-0.5 bg-blue-300 hidden md:block" />

//           {/* Video Cards */}
//           <div className="space-y-12">
//             {videos.map((video, index) => {
//               const isRight = index % 2 === 0;
              
//               return (
//                 <div key={video.id} className="relative">
//                   {/* Timeline Dot */}
//                   <div className="absolute left-1/2 transform -translate-x-1/2 w-4 h-4 bg-blue-500 rounded-full border-4 border-white shadow-md z-10 hidden md:block" />

//                   {/* Card Container */}
//                   <div className={`flex flex-col md:flex-row gap-6 items-center ${isRight ? 'md:flex-row-reverse' : ''}`}>
//                     {/* Video Section */}
//                     <div className="md:w-1/2 flex justify-end">
//                       <div className="w-full max-w-sm">
//                         <div className="bg-white rounded-xl border-4 border-dashed border-blue-400 p-3 shadow-lg">
//                           <div className="aspect-video bg-gray-800 rounded-lg relative overflow-hidden">
//                             <iframe
//                               src={video.url}
//                               title={video.title}
//                               className="w-full h-full"
//                               allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
//                               allowFullScreen
//                             />
//                           </div>
//                         </div>
//                       </div>
//                     </div>

//                     {/* Description Section */}
//                     <div className="md:w-1/2">
//                       <div className="bg-white rounded-xl p-6 shadow-md border border-gray-200 max-w-sm">
//                         <h3 className="text-2xl font-bold text-gray-800 mb-3">
//                           {video.title}
//                         </h3>
                        
//                         <p className="text-gray-600 leading-relaxed text-sm mb-4">
//                           {video.description}
//                         </p>

//                         <div className="flex items-center justify-between">
//                           <span className="text-sm text-gray-500 flex items-center gap-1">
//                             <Clock size={14} />
//                             {video.duration}
//                           </span>
//                           <button className="bg-blue-500 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-blue-600 transition-colors flex items-center gap-2">
//                             <PlayCircle size={16} />
//                             Watch
//                           </button>
//                         </div>
//                       </div>
//                     </div>
//                   </div>
//                 </div>
//               );
//             })}
//           </div>
//         </div>
//       </main>
//     </div>
//   );
// }
// export default Learning;
// import  { useEffect, useMemo } from 'react';
// import { useDispatch, useSelector } from 'react-redux';
// import { PlayCircle, Clock, CheckCircle, Loader } from 'lucide-react';
// import { 
//   getVideosWithProgress, 
//   markVideoAsComplete, 
//   updateVideoWatch 
// } from '@/Redux/LearningPath/LearningPathAction';

// const Learning = () => {
//   const dispatch = useDispatch();
  
//   // Optimized selectors - select only what you need
//   const videos = useSelector((state) => state.learningPath?.videos || []);
//   const loading = useSelector((state) => state.learningPath?.loading || false);
//   const error = useSelector((state) => state.learningPath?.error || null);
//   const markingComplete = useSelector((state) => state.learningPath?.markingComplete || false);
//   const jwt = useSelector((state) => state.auth?.jwt || null);

//   useEffect(() => {
//     const token = jwt || localStorage.getItem('token');
//     if (token) {
//       dispatch(getVideosWithProgress(token));
//     }
//   }, [dispatch, jwt]);

//   const handleMarkAsComplete = (videoId) => {
//     const token = jwt || localStorage.getItem('token');
//     if (token) {
//       dispatch(markVideoAsComplete({ videoId, jwt: token }));
//     }
//   };

//   const handleUpdateLastWatched = (videoId) => {
//     const token = jwt || localStorage.getItem('token');
//     if (token) {
//       dispatch(updateVideoWatch({ videoId, jwt: token }));
//     }
//   };

//   // Memoize calculated progress
//   const progressPercentage = useMemo(() => {
//     if (!videos || videos.length === 0) return 0;
//     const completed = videos.filter(v => v.completed).length;
//     return Math.round((completed / videos.length) * 100);
//   }, [videos]);

//   // Memoize completed count
//   const completedCount = useMemo(() => {
//     return videos.filter(v => v.completed).length;
//   }, [videos]);

//   if (loading) {
//     return (
//       <div className="min-h-screen bg-gray-50 flex items-center justify-center">
//         <div className="text-center">
//           <Loader className="animate-spin text-blue-500 mx-auto mb-4" size={48} />
//           <p className="text-gray-600">Loading your learning path...</p>
//         </div>
//       </div>
//     );
//   }

//   return (
//     <div className="min-h-screen bg-gray-50">
//       {/* Header */}
//       <header className="bg-white shadow-sm border-b">
//         <div className="max-w-5xl mx-auto px-6 py-5">
//           <div className="flex justify-between items-center">
//             <div>
//               <h1 className="text-3xl font-bold text-gray-800">Learning Path</h1>
//               <p className="text-sm text-gray-600 mt-1">Track your progress as you learn</p>
//             </div>
//             <div className="text-right">
//               <div className="text-2xl font-bold text-blue-600">{progressPercentage}%</div>
//               <div className="text-xs text-gray-600">Complete</div>
//             </div>
//           </div>
          
//           {/* Progress Bar */}
//           <div className="mt-4 bg-gray-200 rounded-full h-2 overflow-hidden">
//             <div 
//               className="bg-gradient-to-r from-blue-500 to-green-500 h-full transition-all duration-500"
//               style={{ width: `${progressPercentage}%` }}
//             />
//           </div>
//         </div>
//       </header>

//       {/* Error Message */}
//       {error && (
//         <div className="max-w-5xl mx-auto px-6 mt-6">
//           <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-800">
//             <p className="text-sm">⚠️ Error: {error}</p>
//           </div>
//         </div>
//       )}

//       {/* No Videos Message */}
//       {!loading && videos.length === 0 && (
//         <div className="max-w-5xl mx-auto px-6 mt-20">
//           <div className="text-center bg-white rounded-xl p-12 shadow-md">
//             <PlayCircle className="mx-auto mb-4 text-gray-400" size={64} />
//             <h3 className="text-2xl font-bold text-gray-800 mb-2">No Videos Available</h3>
//             <p className="text-gray-600">Check back later for learning content!</p>
//           </div>
//         </div>
//       )}

//       {/* Learning Path Timeline */}
//       {videos.length > 0 && (
//         <main className="max-w-5xl mx-auto px-6 py-10">
//           <div className="relative">
//             {/* Central Thread Line */}
//             <div className="absolute left-1/2 transform -translate-x-1/2 top-0 bottom-0 w-0.5 bg-blue-300 hidden md:block" />

//             {/* Video Cards */}
//             <div className="space-y-12">
//               {videos.map((video, index) => {
//                 const isRight = index % 2 === 0;
                
//                 return (
//                   <div key={video.id} className="relative">
//                     {/* Timeline Dot */}
//                     <div className={`absolute left-1/2 transform -translate-x-1/2 w-4 h-4 rounded-full border-4 border-white shadow-md z-10 hidden md:block ${
//                       video.completed ? 'bg-green-500' : 'bg-blue-500'
//                     }`} />

//                     {/* Card Container */}
//                     <div className={`flex flex-col md:flex-row gap-6 items-center ${isRight ? 'md:flex-row-reverse' : ''}`}>
//                       {/* Video Section */}
//                       <div className="md:w-1/2 flex justify-end">
//                         <div className="w-full max-w-sm relative">
//                           {video.completed && (
//                             <div className="absolute -top-3 -right-3 bg-green-500 text-white rounded-full p-2 shadow-lg z-20">
//                               <CheckCircle size={24} />
//                             </div>
//                           )}
//                           <div className={`bg-white rounded-xl border-4 border-dashed p-3 shadow-lg ${
//                             video.completed ? 'border-green-400' : 'border-blue-400'
//                           }`}>
//                             <div 
//                               className="aspect-video bg-gray-800 rounded-lg relative overflow-hidden cursor-pointer"
//                               onClick={() => handleUpdateLastWatched(video.id)}
//                             >
//                               <iframe
//                                 src={video.videoUrl}
//                                 title={video.title}
//                                 className="w-full h-full"
//                                 allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
//                                 allowFullScreen
//                               />
//                             </div>
//                           </div>
//                         </div>
//                       </div>

//                       {/* Description Section */}
//                       <div className="md:w-1/2">
//                         <div className={`bg-white rounded-xl p-6 shadow-md border max-w-sm ${
//                           video.completed ? 'border-green-200 bg-green-50' : 'border-gray-200'
//                         }`}>
//                           <div className="flex items-start justify-between mb-3">
//                             <h3 className="text-2xl font-bold text-gray-800">
//                               {video.title}
//                             </h3>
//                             {video.completed && (
//                               <span className="text-xs bg-green-500 text-white px-2 py-1 rounded-full">
//                                 ✓ Done
//                               </span>
//                             )}
//                           </div>
                          
//                           <p className="text-gray-600 leading-relaxed text-sm mb-4">
//                             {video.description}
//                           </p>

//                           <div className="flex items-center justify-between">
//                             <span className="text-sm text-gray-500 flex items-center gap-1">
//                               <Clock size={14} />
//                               {video.duration}
//                             </span>
//                             {!video.completed ? (
//                               <button 
//                                 onClick={() => handleMarkAsComplete(video.id)}
//                                 disabled={markingComplete}
//                                 className="bg-blue-500 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-blue-600 transition-colors flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
//                               >
//                                 {markingComplete ? (
//                                   <Loader className="animate-spin" size={16} />
//                                 ) : (
//                                   <CheckCircle size={16} />
//                                 )}
//                                 Mark Complete
//                               </button>
//                             ) : (
//                               <button 
//                                 className="bg-green-500 text-white px-4 py-2 rounded-lg text-sm font-semibold cursor-default flex items-center gap-2"
//                               >
//                                 <CheckCircle size={16} />
//                                 Completed
//                               </button>
//                             )}
//                           </div>
//                         </div>
//                       </div>
//                     </div>
//                   </div>
//                 );
//               })}
//             </div>
//           </div>
//         </main>
//       )}

//       {/* Summary Footer */}
//       {videos.length > 0 && (
//         <footer className="mt-16 bg-white border-t py-8">
//           <div className="max-w-5xl mx-auto px-6 text-center">
//             <p className="text-gray-600">
//               <span className="font-semibold text-blue-600">{completedCount}</span> of{' '}
//               <span className="font-semibold text-blue-600">{videos.length}</span> lessons completed
//             </p>
//           </div>
//         </footer>
//       )}
//     </div>
//   );
// };

// export default Learning;