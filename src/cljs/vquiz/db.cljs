(ns vquiz.db)

;; FIXME: Add a spec for the DB

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
