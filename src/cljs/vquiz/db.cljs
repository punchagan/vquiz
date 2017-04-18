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
                              :display-at 5.5 ;; time (seconds) after start
                              }}
               }})

(defn current-question [db]
  (let [qid (:current-question db)]
    (-> db :questions qid :question)))

(defn correct-answer [question]
  (nth (:answers question) (:correct-answer question)))
