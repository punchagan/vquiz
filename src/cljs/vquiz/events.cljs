(ns vquiz.events
    (:require [re-frame.core :as re-frame]
              [vquiz.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))

(def ^:const +video-ended+ 0)
(def ^:const +video-playing+ 1)
(def ^:const +video-paused+ 2)

(def ^:const player-data
  [:youtube-player
   {:height "350"
    :width "480"
    ;; fixme: Hiding controls doesn't seem to be working
    :player-vars {:controls "0" :autoplay "1"}
    :events {:on-ready [:player-ready]
             :on-state-change [:player-state-change]}}])

(re-frame/reg-event-fx
 :initialize-youtube
 (constantly {:youtube/initialize-player player-data}))

(re-frame/reg-event-db
 :initialize-quiz
 (fn [db [_ _] ]
   (assoc db :quiz-started true)))

(re-frame/reg-event-fx
 :load-next-question
 (fn [cofx [_ question]]
   {:youtube/load-video-by-id [:youtube-player question]}))

(re-frame/reg-event-fx
 :player-ready
 (fn [cofx [_ event]]
   (let [db (:db cofx)
         ;; FIXME: Replace stub with actual code
         qid :1]
     {:dispatch [:load-next-question
                 (-> db :questions qid)]
      :db (assoc db
                 :current-question qid
                 :display-question nil
                 :correct-answer nil)})))

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
