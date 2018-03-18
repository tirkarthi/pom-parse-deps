(ns scraper
  (:require [clojure.xml :as xml]
            [cheshire.core :refer :all]
            [monger.core :as mg]
            [monger.collection :as mc]))


(defn dependencies-to-str
  [dependencies]
  (map (fn [item]
         (->> item
              :content
              (mapcat :content)
              (filter string?)
              (take 3)))
       (:content dependencies)))

(defn get-dependencies
  [xml-doc]
  (->> xml-doc
       :content
       (filter #(= (:tag %) :dependencies))
       first))

(defn get-package-meta-data
  [xml-doc]
  (->> (range 8)
       (keep #(get (:content xml-doc) %1))
       (map (juxt :tag (comp first :content)))
       (into {})))

(defn process-file
  [file]
  (try (xml/parse file)
       (catch Exception e (do (println "Culprit file " file) nil))))

(defn process-xml
  [xml-doc]
  (let [meta-data    (get-package-meta-data xml-doc)
        dependencies (get-dependencies xml-doc)
        deps-dict    (mapv #(zipmap [:groupId :artifactId :version] %)
                           (dependencies-to-str dependencies))]
    (assoc meta-data :dependencies deps-dict)))

(defn -main []
  (let [path  "/home/ubuntu/my-wonderful-copy-of-clojars/"
        files (->> path
                   clojure.java.io/file
                   file-seq
                   (filter #(clojure.string/ends-with? (.getName %1) ".pom"))
                   (map #(.getAbsolutePath %1))
                   (remove #(re-find #"SNAPSHOT" %1)))
        conn  (mg/connect)
        db    (mg/get-db conn "lein")]
    (->> files
         (keep process-file)
         (map process-xml)
         (partition-all 1000)
         (mapv (fn [item] (mc/insert-batch db "clojars" item))))))
