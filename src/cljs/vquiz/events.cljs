(ns vquiz.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [vquiz.db :as db]))

(def ^:const +video-ended+ 0)
(def ^:const +video-playing+ 1)
(def ^:const +video-paused+ 2)

(def player-data
  {:playerVars {:controls 0
                 :enablejsapi 1
                 :fs 0
                 :modestbranding 1
                 :rel 0
                 :showinfo 0
                 :iv_load_policy 3
                 :disablekb 1}
    :events {:on-ready [:player-ready]
             :on-state-change [:player-state-change]}})


(re-frame/reg-event-fx
 :initialize-db
 (fn [_ [_ url]]
   {:http-xhrio {:method :get
                 :uri url
                 :timeout 8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:quiz-db-fetched]
                 :on-failure [:quiz-db-fetch-failed]}}))

(re-frame/reg-event-db
 :quiz-db-fetched
 (fn [_ [_ data]]
   data))

(re-frame/reg-event-db
 :quiz-db-fetch-failed
 (fn [db _] db))

(re-frame/reg-event-fx
 :initialize-youtube
 (fn [_ [_ [h w]]]
   (let [player-dynamic-data {:height h :width w}
         data (merge player-data player-dynamic-data)]
     {:youtube/initialize-player [:youtube-player data]})))

(re-frame/reg-event-db
 :initialize-quiz
 (fn [db [_ _] ]
   (assoc db :quiz-started true)))

(re-frame/reg-event-fx
 :load-next-question
 (fn [cofx _]
   (let [db (:db cofx)
         qid (db/next-question-id db)]
     (when (some? qid)
       {:youtube/cue-video-by-id [:youtube-player (-> db :questions qid)]
        :db (assoc db
                   ;; fixme: Have a better way of managing all this state -
                   ;; functions to initialize, reset this state?
                   :current-question qid
                   :quiz-progress (db/quiz-progress db)
                   :display-question nil
                   :answer-correct? nil
                   :video-ended? nil)}))))

(re-frame/reg-event-fx
 :player-ready
 (fn [cofx [_ event]]
   {:dispatch [:load-next-question]}))

(re-frame/reg-event-db
 :verify-answer
  (fn [db [_ selected-answer]]
   (let [question (db/current-question db)
         correct-answer (db/correct-answer question)]
     (when selected-answer
       (assoc db :answer-correct? (= (.-value selected-answer) correct-answer))))))

(re-frame/reg-event-fx
 :pause-video
 (fn [_ _] {:youtube/pause-video :youtube-player}))

(re-frame/reg-event-fx
 :resume-video
 (fn [_ _] {:youtube/play-video :youtube-player}))

(defmulti player-state-change
  (fn [e] (.-data e)))

(defmethod player-state-change +video-playing+
  [_ db]
  (let [question (db/current-question db)
        answer-correct? (:answer-correct? db)
        display-at (:display-at question)]
    (when (nil? answer-correct?)
      {:dispatch-later [{:ms (* 1000 display-at) :dispatch [:pause-video]}
                        {:ms (* 1000 display-at) :dispatch [:display-question]}]})))

(defmethod player-state-change +video-ended+
  [_ db]
  {:db (assoc db :video-ended? true)})

(defmethod player-state-change :default
  [e _]
  (println "player state change" (.-data e))
  {})

(re-frame/reg-event-fx
 :player-state-change
 (fn [cofx [_ event]]
   (let [db (:db cofx)]
     (player-state-change event db))))

(re-frame/reg-event-db
 :display-question
 (fn [db [_ event]]
   (let [question (db/current-question db)]
     (assoc db :display-question question))))
