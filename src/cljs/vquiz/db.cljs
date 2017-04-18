(ns vquiz.db)

(def default-db
  {:quiz-title "Welcome to VQuiz"
   :quiz-intro "This is a quiz created by VQuiz. Click on start to begin the quiz!"
   :questions {
               :1 {:video-id "gXq-14lV79s"
                   :start-seconds 10
                   :end-seconds 20
                   :question {:text "What is going to happen next?"
                              :answers ["This"
                                        "That"
                                        "Other thing"
                                        "All of the above"]
                              ;; :type :mcq -- this is the only type for now
                              :correct-answer 1  ;; index in answers
                              :display-at 1 ;; time (seconds) after start
                              }}
               :2 {:video-id "gXq-14lV79s"
                   :start-seconds 100
                   :end-seconds 120
                   :question {:text "This is the next question, huh?"
                              :answers ["Fine"
                                        "Hell, yeah!"
                                        "No way!"
                                        "All of the above"]
                              ;; :type :mcq -- this is the only type for now
                              :correct-answer 1  ;; index in answers
                              :display-at 2 ;; time (seconds) after start
                              }}
               }})


(defn next-question-id [db]
  (let [current-id (:current-question db)
        questions (into (sorted-map) (:questions db))
        qids (keys questions)]

    (if (nil? current-id)
      (first qids)
      (first (drop-while (fn [x] (<= x current-id)) qids)))))

(defn current-question [db]
  (let [qid (:current-question db)]
    (-> db :questions qid :question)))

(defn correct-answer [question]
  (nth (:answers question) (:correct-answer question)))
