(ns vquiz.events
    (:require [re-frame.core :as re-frame]
              [vquiz.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))

(def ^:const player-data
  [:youtube-player
   {:height "350"
    :width "480"
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
 :show-next-question
 (fn [cofx [_ id]]
   {:youtube/load-video-by-id [:youtube-player id]}))

(re-frame/reg-event-fx
 :player-ready
 (fn [cofx [_ event]]
   (let [db (:db cofx)]
     {:dispatch [:show-next-question
                 ;; FIXME: Get the video id based on current question
                 (-> db :videos first)]})))

(re-frame/reg-event-fx
 :player-state-change
 (fn [_ [_ event]]
   (println "player changed" event)
   {}))
