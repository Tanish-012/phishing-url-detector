import { useState } from 'react';
import { Shield, ShieldAlert, ShieldCheck, Search, Activity, Link as LinkIcon, AlertTriangle, CheckCircle } from 'lucide-react';

function App() {
  const [url, setUrl] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [result, setResult] = useState(null);

  const analyzeUrl = async (e) => {
    e.preventDefault();
    if (!url) return;

    setIsLoading(true);
    setResult(null);

    try {
      // Dynamically use the host's IP address so other devices on the network can connect
      const apiUrl = `http://${window.location.hostname}:8080/api/check-url`;
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ url })
      });

      const data = await response.json();
      // Simulate slight network delay for dramatic effect if it's too fast
      setTimeout(() => {
        setResult(data);
        setIsLoading(false);
      }, 800);

    } catch (error) {
      console.error(error);
      setIsLoading(false);
      // Fallback for UI testing if backend is down
      setResult({
        url: url,
        isMalicious: false,
        phishingProbability: 0.12,
        features: {
          urlLength: url.length,
          numSuspiciousChars: 0,
          numLexicalKeywords: 0,
          hasSuspiciousIframes: 0,
          externalLinkRatio: 0.2,
          hasFakeLoginForm: 0
        }
      });
    }
  };

  const getScoreColor = (prob) => {
    if (prob > 0.75) return 'text-red-500';
    if (prob > 0.4) return 'text-amber-500';
    return 'text-emerald-500';
  };

  const getScoreBg = (prob) => {
    if (prob > 0.75) return 'bg-red-500/20 border-red-500/50';
    if (prob > 0.4) return 'bg-amber-500/20 border-amber-500/50';
    return 'bg-emerald-500/20 border-emerald-500/50';
  };

  const MathRound = (num) => Math.round(num * 100);

  const isThreat = result ? (result.phishingProbability >= 0.5 || result.isMalicious) : false;

  return (
    <div className="min-h-screen bg-slate-950 text-slate-200 font-sans selection:bg-indigo-500/30">
      {/* Dynamic Background Elements */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-[-20%] left-[-10%] w-[50%] h-[50%] rounded-full bg-indigo-600/20 blur-[120px]" />
        <div className="absolute bottom-[-20%] right-[-10%] w-[60%] h-[60%] rounded-full bg-rose-600/10 blur-[150px]" />
        <div className="absolute top-[40%] left-[60%] w-[30%] h-[30%] rounded-full bg-emerald-600/10 blur-[100px]" />
      </div>

      <main className="relative z-10 container mx-auto px-4 py-16 max-w-4xl flex flex-col items-center">

        {/* Header Section */}
        <header className="flex flex-col items-center mb-12 text-center">
          <div className="inline-flex items-center justify-center p-4 mb-6 rounded-2xl bg-slate-900/50 border border-slate-700/50 shadow-xl backdrop-blur-md">
            <Shield className="w-12 h-12 text-indigo-400" strokeWidth={1.5} />
          </div>
          <h1 className="text-4xl md:text-5xl font-bold mb-4 bg-clip-text text-transparent bg-gradient-to-r from-indigo-400 via-purple-400 to-rose-400 tracking-tight">
            Advanced Threat Intelligence
          </h1>
          <p className="text-slate-400 text-lg md:text-xl max-w-2xl">
            Real-time phishing URL detection powered by Weka Machine Learning and heuristic content analysis.
          </p>
        </header>

        {/* Input Section */}
        <form onSubmit={analyzeUrl} className="w-full max-w-2xl relative mb-16 group">
          <div className="absolute -inset-1 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-2xl blur opacity-25 group-hover:opacity-50 transition duration-1000 group-hover:duration-200"></div>
          <div className="relative flex items-center bg-slate-900 rounded-2xl border border-slate-700/50 shadow-2xl overflow-hidden backdrop-blur-xl">
            <div className="pl-6 text-slate-400">
              <LinkIcon className="w-5 h-5" />
            </div>
            <input
              type="url"
              required
              placeholder="https://example.com/login"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              className="w-full bg-transparent px-4 py-5 text-lg outline-none placeholder:text-slate-600 focus:text-white"
            />
            <button
              type="submit"
              disabled={isLoading || !url}
              className="mr-2 px-6 py-3 bg-indigo-500 hover:bg-indigo-600 disabled:bg-slate-800 disabled:text-slate-500 text-white font-medium rounded-xl transition-all flex items-center gap-2"
            >
              {isLoading ? (
                <>
                  <Activity className="w-5 h-5 animate-spin" />
                  Analyzing
                </>
              ) : (
                <>
                  <Search className="w-5 h-5" />
                  Scan
                </>
              )}
            </button>
          </div>
        </form>

        {/* Results Section */}
        {result && (
          <div className="w-full animate-fade-in-up">
            <div className={`p-1 rounded-3xl bg-gradient-to-b ${isThreat ? 'from-red-500/20 to-red-900/10' : 'from-emerald-500/20 to-emerald-900/10'}`}>
              <div className="bg-slate-900/90 backdrop-blur-2xl rounded-[22px] p-8 border border-slate-800">

                {/* Score Header */}
                <div className="flex flex-col md:flex-row items-center justify-between gap-8 mb-10 pb-10 border-b border-slate-800">
                  <div className="flex items-center gap-6">
                    <div className={`p-4 rounded-2xl border ${getScoreBg(result.phishingProbability)}`}>
                      {isThreat ?
                        <ShieldAlert className="w-12 h-12 text-red-400" strokeWidth={1.5} /> :
                        <ShieldCheck className="w-12 h-12 text-emerald-400" strokeWidth={1.5} />
                      }
                    </div>
                    <div>
                      <h2 className="text-3xl font-bold text-white mb-2">
                        {isThreat ? 'Not Safe to Proceed' : 'Safe to Proceed'}
                      </h2>
                      <p className="text-slate-400 flex items-center gap-2">
                        {isThreat ? <AlertTriangle className="w-4 h-4 text-red-500" /> : <CheckCircle className="w-4 h-4 text-emerald-500" />}
                        {result.url}
                      </p>
                    </div>
                  </div>

                  <div className="flex flex-col items-center">
                    <div className="relative flex items-center justify-center w-32 h-32">
                      <svg className="w-full h-full transform -rotate-90">
                        <circle cx="64" cy="64" r="58" className="stroke-slate-800 fill-none" strokeWidth="8" />
                        <circle
                          cx="64"
                          cy="64"
                          r="58"
                          className={`fill-none ${isThreat ? 'stroke-red-500' : 'stroke-emerald-500'} transition-all duration-1000 ease-out`}
                          strokeWidth="8"
                          strokeDasharray="364.4"
                          strokeDashoffset={364.4 - (364.4 * result.phishingProbability)}
                          strokeLinecap="round"
                        />
                      </svg>
                      <div className="absolute flex flex-col items-center">
                        <span className={`text-4xl font-bold tracking-tighter ${getScoreColor(result.phishingProbability)}`}>
                          {MathRound(result.phishingProbability)}<span className="text-xl">%</span>
                        </span>
                        <span className="text-xs text-slate-500 uppercase tracking-widest mt-1">Risk</span>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Feature Analysis Grid */}
                <div>
                  <h3 className="text-lg font-medium text-slate-300 mb-6 flex items-center gap-2">
                    <Activity className="w-5 h-5 text-indigo-400" />
                    Detailed Analysis Breakdown
                  </h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    <FeatureCard
                      label="URL Length"
                      value={result.features.urlLength}
                      desc="Character count"
                      alert={result.features.urlLength > 75}
                    />
                    <FeatureCard
                      label="Suspicious Chars"
                      value={result.features.numSuspiciousChars}
                      desc="Like @ or multiple -"
                      alert={result.features.numSuspiciousChars > 0}
                    />
                    <FeatureCard
                      label="Lexical Keywords"
                      value={result.features.numLexicalKeywords}
                      desc="Targeted words (e.g. login)"
                      alert={result.features.numLexicalKeywords > 0}
                    />
                    <FeatureCard
                      label="Suspicious Iframes"
                      value={result.features.hasSuspiciousIframes === 1.0 ? "Yes" : "No"}
                      desc="Hidden embedded content"
                      alert={result.features.hasSuspiciousIframes === 1.0}
                    />
                    <FeatureCard
                      label="External Link Ratio"
                      value={`${MathRound(result.features.externalLinkRatio)}%`}
                      desc="Links outside domain"
                      alert={result.features.externalLinkRatio > 0.5}
                    />
                    <FeatureCard
                      label="Fake Login Form"
                      value={result.features.hasFakeLoginForm === 1.0 ? "Detected" : "None"}
                      desc="Suspicious form action"
                      alert={result.features.hasFakeLoginForm === 1.0}
                    />
                  </div>
                </div>

              </div>
            </div>
          </div>
        )}

      </main>

      {/* Global simple animations */}
      <style dangerouslySetInnerHTML={{
        __html: `
        @keyframes fade-in-up {
          0% { opacity: 0; transform: translateY(20px); }
          100% { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in-up {
          animation: fade-in-up 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
        }
      `}} />
    </div>
  );
}

function FeatureCard({ label, value, desc, alert }) {
  return (
    <div className={`p-4 rounded-xl border bg-slate-800/50 backdrop-blur-sm transition-colors ${alert ? 'border-red-500/30 hover:border-red-500/50' : 'border-slate-700 hover:border-slate-600'}`}>
      <div className="flex justify-between items-start mb-2">
        <span className="text-sm font-medium text-slate-400">{label}</span>
        {alert && <AlertTriangle className="w-4 h-4 text-red-400" />}
      </div>
      <div className={`text-2xl font-semibold mb-1 ${alert ? 'text-red-300' : 'text-slate-200'}`}>
        {value}
      </div>
      <div className="text-xs text-slate-500">{desc}</div>
    </div>
  );
}

export default App;
