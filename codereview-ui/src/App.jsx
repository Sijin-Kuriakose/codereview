import { useState } from "react";
import axios from "axios";
import "./App.css";

const API = "http://localhost:8080";

export default function App() {
  const [repoUrl, setRepoUrl] = useState("");
  const [status, setStatus] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const startAnalysis = async () => {
    if (!repoUrl.trim()) {
      setError("Enter a GitHub repo URL");
      return;
    }

    // 🔥 HARD RESET (this was your missing piece)
    setResult(null);
    setStatus("");
    setError("");
    setLoading(true);

    try {
      const res = await axios.post(`${API}/analyze`, {
        repoUrl: repoUrl.trim(),
      });

      pollJob(res.data.jobId);
    } catch {
      setError("Backend not running / CORS issue");
      setLoading(false);
    }
  };

  const pollJob = (id) => {
    const interval = setInterval(async () => {
      try {
        const res = await axios.get(`${API}/job/${id}`);
        const data = res.data;

        setStatus(data.status);

        // 🔥 ONLY SET RESULT WHEN FULLY READY
        if (data.status === "COMPLETED" && data.issues) {
          setResult(data);
          setLoading(false);
          clearInterval(interval);
        }

        if (data.status === "FAILED") {
          setError("Analysis failed");
          setLoading(false);
          clearInterval(interval);
        }
      } catch {
        setError("Error fetching job");
        setLoading(false);
        clearInterval(interval);
      }
    }, 2000);
  };

  return (
    <div className="app">

      <h1>AI Code Review Dashboard</h1>

      {/* INPUT */}
      <div className="input-box">
        <input
          type="text"
          placeholder="Enter GitHub repo URL"
          value={repoUrl}
          onChange={(e) => setRepoUrl(e.target.value)}
        />
        <button onClick={startAnalysis}>
          {loading ? "Analyzing..." : "Analyze"}
        </button>
      </div>

      {/* LOADING STATE */}
      {loading && (
        <div className="loading">
          <p>Analyzing repository...</p>
          <p>Status: {status}</p>
        </div>
      )}

      {/* ERROR */}
      {error && <p className="error">{error}</p>}

      {/* ✅ ONLY SHOW WHEN COMPLETE DATA EXISTS */}
      {result && result.issues && (
        <>
          {/* METRICS */}
          <div className="cards">
            <div className="card"><h3>Score</h3><p>{result.score}</p></div>
            <div className="card"><h3>Grade</h3><p>{result.grade}</p></div>
            <div className="card"><h3>Total</h3><p>{result.totalIssues}</p></div>
            <div className="card high"><h3>High</h3><p>{result.highCount}</p></div>
            <div className="card medium"><h3>Medium</h3><p>{result.mediumCount}</p></div>
            <div className="card low"><h3>Low</h3><p>{result.lowCount}</p></div>
          </div>

          {/* SUMMARY */}
          <div className="summary">
            <h3>Summary</h3>
            <p>{result.analysisSummary}</p>
          </div>

          {/* ISSUES */}
          <div className="issues-section">
            <h2>Detected Issues</h2>

            {result.issues.map((issue, i) => (
              <div key={i} className="issue-card">

                <div className="issue-header">
                  <span className={`badge ${issue.severity?.toLowerCase()}`}>
                    {issue.severity}
                  </span>
                  <span className="type">{issue.type}</span>
                </div>

                <p><b>File:</b> {issue.file} (Line: {issue.line})</p>

                <p><b>Problem:</b> {issue.message}</p>

                <p><b>Suggestion:</b> {issue.suggestion}</p>

                <p><b>Context:</b> {issue.context}</p>

                {issue.codeSnippet && (
                  <>
                    <p><b>Code:</b></p>
                    <pre className="code-block">{issue.codeSnippet}</pre>
                  </>
                )}

                <div className="ai-box">
                  <p><b>AI Explanation:</b></p>
                  <p>{issue.aiExplanation || "Not available"}</p>

                  <p><b>AI Fix:</b></p>
                  <p>{issue.aiSuggestion || "Not available"}</p>
                </div>

              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}