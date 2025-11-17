import { API_BASE_URL } from "@/Api/api";
import api from "@/Api/api";
import { GET_VIDEOS_REQUEST,GET_VIDEOS_SUCCESS,GET_VIDEOS_FAILURE,MARK_VIDEO_COMPLETE_FAILURE,MARK_VIDEO_COMPLETE_SUCCESS,MARK_VIDEO_COMPLETE_REQUEST,UPDATE_VIDEO_WATCH_FAILURE,UPDATE_VIDEO_WATCH_SUCCESS,UPDATE_VIDEO_WATCH_REQUEST } from "./ActionType";

// Get all videos with user progress
export const getVideosWithProgress = (jwt) => async (dispatch) => {
  dispatch({ type: GET_VIDEOS_REQUEST });

  try {
    const response = await api.get(`${API_BASE_URL}/api/learning-path/videos`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
    dispatch({
      type: GET_VIDEOS_SUCCESS,
      payload: response.data,
    });
    console.log("videos with progress --- ", response.data);
  } catch (error) {
    dispatch({
      type: GET_VIDEOS_FAILURE,
      payload: error.message,
    });
  }
};

// Mark video as completed
export const markVideoAsComplete =
  ({ videoId, jwt }) =>
  async (dispatch) => {
    dispatch({ type: MARK_VIDEO_COMPLETE_REQUEST });

    try {
      const response = await api.post(
        `${API_BASE_URL}/api/learning-path/videos/${videoId}/complete`,
        {},
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        }
      );
      dispatch({
        type: MARK_VIDEO_COMPLETE_SUCCESS,
        payload: videoId,
      });
      console.log("video marked complete --- ", videoId);
    } catch (error) {
      dispatch({
        type: MARK_VIDEO_COMPLETE_FAILURE,
        payload: error.message,
      });
    }
  };

// Update last watched timestamp
export const updateVideoWatch =
  ({ videoId, jwt }) =>
  async (dispatch) => {
    dispatch({ type: UPDATE_VIDEO_WATCH_REQUEST });

    try {
      await api.post(
        `${API_BASE_URL}/api/learning-path/videos/${videoId}/watch`,
        {},
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        }
      );
      dispatch({
        type: UPDATE_VIDEO_WATCH_SUCCESS,
        payload: videoId,
      });
    } catch (error) {
      dispatch({
        type: UPDATE_VIDEO_WATCH_FAILURE,
        payload: error.message,
      });
    }
  };