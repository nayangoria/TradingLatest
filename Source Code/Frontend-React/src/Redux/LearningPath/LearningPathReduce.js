import * as types from "./ActionTypes";

const initialState = {
  videos: [],
  loading: false,
  error: null,
  markingComplete: false,
  updatingWatch: false,
};

const learningPathReducer = (state = initialState, action) => {
  switch (action.type) {
    // Get Videos
    case types.GET_VIDEOS_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case types.GET_VIDEOS_SUCCESS:
      return {
        ...state,
        loading: false,
        videos: action.payload,
        error: null,
      };
    case types.GET_VIDEOS_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    // Mark Video Complete
    case types.MARK_VIDEO_COMPLETE_REQUEST:
      return {
        ...state,
        markingComplete: true,
        error: null,
      };
    case types.MARK_VIDEO_COMPLETE_SUCCESS:
      return {
        ...state,
        markingComplete: false,
        videos: state.videos.map((video) =>
          video.id === action.payload
            ? { ...video, completed: true, completedAt: new Date().toISOString() }
            : video
        ),
        error: null,
      };
    case types.MARK_VIDEO_COMPLETE_FAILURE:
      return {
        ...state,
        markingComplete: false,
        error: action.payload,
      };

    // Update Video Watch
    case types.UPDATE_VIDEO_WATCH_REQUEST:
      return {
        ...state,
        updatingWatch: true,
      };
    case types.UPDATE_VIDEO_WATCH_SUCCESS:
      return {
        ...state,
        updatingWatch: false,
        videos: state.videos.map((video) =>
          video.id === action.payload
            ? { ...video, lastWatchedAt: new Date().toISOString() }
            : video
        ),
      };
    case types.UPDATE_VIDEO_WATCH_FAILURE:
      return {
        ...state,
        updatingWatch: false,
        error: action.payload,
      };

    default:
      return state;
  }
};

export default learningPathReducer;