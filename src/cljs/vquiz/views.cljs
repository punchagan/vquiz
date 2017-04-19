(ns vquiz.views
  (:require [re-frame.core :as re-frame]
            [vquiz.db :as db]))

(defn initialize-quiz-ui []
  (re-frame/dispatch [:initialize-youtube])
  (re-frame/dispatch [:initialize-quiz]))

(defn start-button []
  [:a {:class "btn btn-primary btn-lg"
       :role "button"
       :href "#"
       :on-click initialize-quiz-ui}
   "Start Quiz!"])

(defn make-id [group value]
  "Return id given a group and value."
  (str group "-" value))

(defn radio-button [name value]
  ^{:key (make-id name value)}
  [:div {:class "input-group"}
   [:span {:class "input-group-addon"}
    [:input {:type "radio" :name name :value value}]]
   [:span
    {:on-click (fn [e] (aset (-> e .-target .-previousSibling .-firstChild) "checked" true))
     :class "form-control"}
    value]])

(defn verify-answer [e]
  (let [selected-answer (js/document.querySelector "input[name=answers]:checked")]
    (when selected-answer
      (re-frame/dispatch [:verify-answer selected-answer]))))

(defn resume-video []
  (re-frame/dispatch [:resume-video]))

(defn load-next-question []
  (re-frame/dispatch [:load-next-question]))

(defn display [question answer-correct? video-ended?]
  [:div
   [:p {:class "lead"} (:text question)]
   (let [correct-answer (db/correct-answer question)]
     (cond
       (nil? answer-correct?)
       [:div {:class "form-group"}
        (for [ans (:answers question)]
          (radio-button "answers" ans))]
       (true? answer-correct?)
       [:p {:class "alert alert-success"} (str "Correct! The answer is " correct-answer)]
       (false? answer-correct?)
       [:p {:class "alert alert-danger"} (str "Sorry! The correct answer is " correct-answer)]))

   (if (nil? answer-correct?)
     [:button {:class "btn btn-primary"
               :on-click verify-answer} "Check Answer"]
     [:span {:class "btn-group"}
      [:button {:class "btn btn-primary"
                :on-click load-next-question}
       "Next Question"]
      (when (not video-ended?)
        [:button {:on-click resume-video
                  :class "btn btn-secondary"} "Resume Video"])])])

(defn youtube-player-initialized? []
  (let [youtube-player (js/document.querySelector "#youtube-player")]
    (and youtube-player
         (= (.-tagName youtube-player) "IFRAME"))))

(defn intro-panel [intro-text]
  [:div
   [:p {:class "lead"} intro-text]
   [start-button]])

(defn main-panel []
  (let [quiz-title (re-frame/subscribe [:quiz-title])
        quiz-intro (re-frame/subscribe [:quiz-intro])
        quiz-started (re-frame/subscribe [:quiz-started])
        display-question (re-frame/subscribe [:display-question])
        answer-correct? (re-frame/subscribe [:answer-correct?])
        video-ended? (re-frame/subscribe [:video-ended?])]
    (fn []
      (if (some? @quiz-title)
        [:div
         [:h1 {:class "display-4"} @quiz-title]
         [:div {:id :youtube-player}]
         (if (not @quiz-started)
           [intro-panel @quiz-intro]
           ;; Youtube player gets initialized in an on-click method, but
           ;; developing with figwheel breaks this, and hence this hack
           (when (not (youtube-player-initialized?))
             (initialize-quiz-ui)))
         (when @display-question
           (display @display-question @answer-correct? @video-ended?))]
        [:p "Loading ..."]))))
