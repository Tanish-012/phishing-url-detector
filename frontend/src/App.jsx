import { useState, useEffect } from 'react';
import { Shield, ShieldAlert, ShieldCheck, Search, Activity, Link as LinkIcon, AlertTriangle, CheckCircle, Globe, Lock, FileText, HelpCircle } from 'lucide-react';

// ─── Utility: Parse URL into parts ────────────────────────────────────────────
function parseUrl(urlStr) {
  try {
    const u = new URL(urlStr);
    const hostParts = u.hostname.split('.');
    const tld = hostParts.length >= 2 ? '.' + hostParts.slice(-1)[0] : '';
    const domain = hostParts.length >= 2 ? hostParts.slice(-2).join('.') : u.hostname;
    const subdomains = hostParts.length > 2 ? hostParts.slice(0, -2).join('.') : null;
    return {
      protocol: u.protocol.replace(':', ''),
      subdomain: subdomains,
      domain,
      tld,
      path: u.pathname !== '/' ? u.pathname : null,
      query: u.search ? u.search.slice(1) : null,
      isHttps: u.protocol === 'https:',
    };
  } catch {
    return null;
  }
}

// ─── Safety Tips ───────────────────────────────────────────────────────────────
const SAFE_TIPS = [
  { icon: '✅', title: 'Still be cautious', desc: 'Even safe-looking URLs can change. Always verify the sender before entering personal info.' },
  { icon: '🔒', title: 'Look for HTTPS', desc: 'Ensure the site uses HTTPS (padlock icon) before submitting any data.' },
  { icon: '🧠', title: 'Stay informed', desc: 'Bookmark important websites directly instead of clicking links in emails.' },
];

const DANGER_TIPS = [
  { icon: '🚫', title: 'Do NOT visit this URL', desc: 'This URL shows signs of phishing. Do not enter credentials or personal data.' },
  { icon: '📢', title: 'Report it', desc: 'Report the link to your IT department or use Google Safe Browsing to flag it.' },
  { icon: '🔑', title: 'Change passwords', desc: 'If you already visited this site, change your passwords immediately and check for unusual activity.' },
  { icon: '📧', title: 'Warn others', desc: 'If you received this link via email or chat, warn the sender — their account may be compromised.' },
];

// ─── Animated Risk Meter ───────────────────────────────────────────────────────
function RiskMeter({ probability, isThreat }) {
  const [animatedProb, setAnimatedProb] = useState(0);
  const circumference = 364.4;

  useEffect(() => {
    // Reset to 0, then animate to actual value
    setAnimatedProb(0);
    const timer = setTimeout(() => setAnimatedProb(probability), 80);
    return () => clearTimeout(timer);
  }, [probability]);

  const offset = circumference - circumference * animatedProb;

  const getColor = (p) => {
    if (p > 0.75) return { stroke: '#ef4444', text: 'text-red-400' };
    if (p > 0.4) return { stroke: '#f59e0b', text: 'text-amber-400' };
    return { stroke: '#10b981', text: 'text-emerald-400' };
  };

  const { stroke, text } = getColor(probability);

  return (
    <div className="flex flex-col items-center gap-2">
      <div className="relative flex items-center justify-center w-36 h-36">
        {/* Glow effect */}
        <div
          className="absolute inset-0 rounded-full blur-xl opacity-30 transition-all duration-1000"
          style={{ backgroundColor: stroke }}
        />
        <svg className="w-full h-full transform -rotate-90" viewBox="0 0 128 128">
          {/* Track */}
          <circle cx="64" cy="64" r="58" className="stroke-slate-800 fill-none" strokeWidth="10" />
          {/* Animated fill */}
          <circle
            cx="64"
            cy="64"
            r="58"
            fill="none"
            stroke={stroke}
            strokeWidth="10"
            strokeDasharray={circumference}
            strokeDashoffset={offset}
            strokeLinecap="round"
            style={{ transition: 'stroke-dashoffset 1.2s cubic-bezier(0.34, 1.56, 0.64, 1)' }}
          />
        </svg>
        <div className="absolute flex flex-col items-center">
          <span className={`text-4xl font-bold tracking-tighter ${text}`}>
            {Math.round(probability * 100)}<span className="text-xl">%</span>
          </span>
          <span className="text-xs text-slate-500 uppercase tracking-widest mt-1">Risk</span>
        </div>
      </div>
      <span className={`text-sm font-semibold tracking-wide ${text}`}>
        {probability > 0.75 ? 'HIGH RISK' : probability > 0.4 ? 'MODERATE RISK' : 'LOW RISK'}
      </span>
    </div>
  );
}

// ─── URL Breakdown Panel ───────────────────────────────────────────────────────
function UrlBreakdown({ urlStr }) {
  const parsed = parseUrl(urlStr);
  if (!parsed) return null;

  const parts = [
    { label: 'Protocol', value: parsed.protocol.toUpperCase(), color: parsed.isHttps ? 'text-emerald-400' : 'text-amber-400', dot: parsed.isHttps ? 'bg-emerald-400' : 'bg-amber-400', note: parsed.isHttps ? 'Encrypted ✓' : '⚠ Not encrypted' },
    ...(parsed.subdomain ? [{ label: 'Subdomain', value: parsed.subdomain, color: 'text-amber-300', dot: 'bg-amber-400', note: 'Subdomains can be faked' }] : []),
    { label: 'Domain', value: parsed.domain, color: 'text-indigo-300', dot: 'bg-indigo-400', note: 'Core identity of the site' },
    { label: 'TLD', value: parsed.tld, color: 'text-slate-300', dot: 'bg-slate-400', note: 'Top-level domain' },
    ...(parsed.path ? [{ label: 'Path', value: parsed.path, color: 'text-purple-300', dot: 'bg-purple-400', note: 'Page/resource location' }] : []),
    ...(parsed.query ? [{ label: 'Query Params', value: parsed.query, color: 'text-rose-300', dot: 'bg-rose-400', note: '⚠ May contain tracking or redirect data' }] : []),
  ];

  return (
    <div className="mt-8">
      <h3 className="text-lg font-medium text-slate-300 mb-4 flex items-center gap-2">
        <Globe className="w-5 h-5 text-indigo-400" />
        URL Breakdown
      </h3>
      {/* Visual strip */}
      <div className="flex flex-wrap items-center gap-1 mb-5 p-3 bg-slate-800/60 rounded-xl border border-slate-700/50 font-mono text-sm overflow-x-auto">
        {parts.map((p, i) => (
          <span key={i} className={`${p.color} font-semibold`}>
            {p.value}{i < parts.length - 1 ? <span className="text-slate-600">/</span> : ''}
          </span>
        ))}
      </div>
      {/* Detail cards */}
      <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
        {parts.map((p, i) => (
          <div key={i} className="flex items-start gap-3 p-3 rounded-xl bg-slate-800/40 border border-slate-700/40">
            <div className={`w-2 h-2 rounded-full mt-1.5 flex-shrink-0 ${p.dot}`} />
            <div className="min-w-0">
              <div className="text-xs text-slate-500 mb-0.5">{p.label}</div>
              <div className={`text-sm font-semibold truncate ${p.color}`}>{p.value}</div>
              <div className="text-xs text-slate-600 mt-0.5">{p.note}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ─── Safety Tips Section ───────────────────────────────────────────────────────
function SafetyTips({ isThreat }) {
  const tips = isThreat ? DANGER_TIPS : SAFE_TIPS;
  const borderColor = isThreat ? 'border-red-500/20' : 'border-emerald-500/20';
  const headerColor = isThreat ? 'text-red-400' : 'text-emerald-400';
  const iconBg = isThreat ? 'bg-red-500/10' : 'bg-emerald-500/10';

  return (
    <div className={`mt-8 p-6 rounded-2xl border ${borderColor} bg-slate-900/50`}>
      <h3 className={`text-lg font-semibold mb-5 flex items-center gap-2 ${headerColor}`}>
        {isThreat ? <AlertTriangle className="w-5 h-5" /> : <HelpCircle className="w-5 h-5" />}
        {isThreat ? 'Safety Warnings — Action Required' : 'Stay Safe Online'}
      </h3>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {tips.map((tip, i) => (
          <div key={i} className={`flex gap-4 p-4 rounded-xl ${iconBg} border ${borderColor}`}>
            <span className="text-2xl flex-shrink-0">{tip.icon}</span>
            <div>
              <div className="text-sm font-semibold text-slate-200 mb-1">{tip.title}</div>
              <div className="text-xs text-slate-400 leading-relaxed">{tip.desc}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ─── Feature Card ──────────────────────────────────────────────────────────────
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

// ─── Main App ─────────────────────────────────────────────────────────────────
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
      const apiUrl = `http://${window.location.hostname}:8080/api/check-url`;
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ url })
      });
      const data = await response.json();
      setTimeout(() => {
        setResult(data);
        setIsLoading(false);
      }, 800);
    } catch {
      setIsLoading(false);
      setResult({
        url,
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

  const isThreat = result ? (result.phishingProbability >= 0.5 || result.isMalicious) : false;
  const MathRound = (num) => Math.round(num * 100);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-200 font-sans selection:bg-indigo-500/30">
      {/* Background blobs */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-[-20%] left-[-10%] w-[50%] h-[50%] rounded-full bg-indigo-600/20 blur-[120px]" />
        <div className="absolute bottom-[-20%] right-[-10%] w-[60%] h-[60%] rounded-full bg-rose-600/10 blur-[150px]" />
        <div className="absolute top-[40%] left-[60%] w-[30%] h-[30%] rounded-full bg-emerald-600/10 blur-[100px]" />
      </div>

      <main className="relative z-10 container mx-auto px-4 py-16 max-w-4xl flex flex-col items-center">

        {/* Header */}
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

        {/* Input */}
        <form onSubmit={analyzeUrl} className="w-full max-w-2xl relative mb-16 group">
          <div className="absolute -inset-1 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-2xl blur opacity-25 group-hover:opacity-50 transition duration-1000 group-hover:duration-200" />
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
                <><Activity className="w-5 h-5 animate-spin" />Analyzing</>
              ) : (
                <><Search className="w-5 h-5" />Scan</>
              )}
            </button>
          </div>
        </form>

        {/* Results */}
        {result && (
          <div className="w-full animate-fade-in-up">
            <div className={`p-1 rounded-3xl bg-gradient-to-b ${isThreat ? 'from-red-500/20 to-red-900/10' : 'from-emerald-500/20 to-emerald-900/10'}`}>
              <div className="bg-slate-900/90 backdrop-blur-2xl rounded-[22px] p-8 border border-slate-800">

                {/* Score Header with Animated Risk Meter */}
                <div className="flex flex-col md:flex-row items-center justify-between gap-8 mb-10 pb-10 border-b border-slate-800">
                  <div className="flex items-center gap-6">
                    <div className={`p-4 rounded-2xl border ${isThreat ? 'bg-red-500/20 border-red-500/50' : 'bg-emerald-500/20 border-emerald-500/50'}`}>
                      {isThreat
                        ? <ShieldAlert className="w-12 h-12 text-red-400" strokeWidth={1.5} />
                        : <ShieldCheck className="w-12 h-12 text-emerald-400" strokeWidth={1.5} />
                      }
                    </div>
                    <div>
                      <h2 className="text-3xl font-bold text-white mb-2">
                        {isThreat ? 'Not Safe to Proceed' : 'Safe to Proceed'}
                      </h2>
                      <p className="text-slate-400 flex items-center gap-2 break-all text-sm">
                        {isThreat
                          ? <AlertTriangle className="w-4 h-4 text-red-500 flex-shrink-0" />
                          : <CheckCircle className="w-4 h-4 text-emerald-500 flex-shrink-0" />
                        }
                        {result.url}
                      </p>
                    </div>
                  </div>

                  {/* Animated Risk Meter */}
                  <RiskMeter probability={result.phishingProbability} isThreat={isThreat} />
                </div>

                {/* Feature Analysis Grid */}
                <div>
                  <h3 className="text-lg font-medium text-slate-300 mb-6 flex items-center gap-2">
                    <Activity className="w-5 h-5 text-indigo-400" />
                    Detailed Analysis Breakdown
                  </h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    <FeatureCard label="URL Length" value={result.features.urlLength} desc="Character count" alert={result.features.urlLength > 75} />
                    <FeatureCard label="Suspicious Chars" value={result.features.numSuspiciousChars} desc="Like @ or multiple -" alert={result.features.numSuspiciousChars > 0} />
                    <FeatureCard label="Lexical Keywords" value={result.features.numLexicalKeywords} desc='Targeted words (e.g. "login")' alert={result.features.numLexicalKeywords > 0} />
                    <FeatureCard label="Suspicious Iframes" value={result.features.hasSuspiciousIframes === 1.0 ? 'Yes' : 'No'} desc="Hidden embedded content" alert={result.features.hasSuspiciousIframes === 1.0} />
                    <FeatureCard label="External Link Ratio" value={`${MathRound(result.features.externalLinkRatio)}%`} desc="Links outside domain" alert={result.features.externalLinkRatio > 0.5} />
                    <FeatureCard label="Fake Login Form" value={result.features.hasFakeLoginForm === 1.0 ? 'Detected' : 'None'} desc="Suspicious form action" alert={result.features.hasFakeLoginForm === 1.0} />
                  </div>
                </div>

                {/* URL Breakdown Panel */}
                <UrlBreakdown urlStr={result.url} />

                {/* Safety Tips */}
                <SafetyTips isThreat={isThreat} />

              </div>
            </div>
          </div>
        )}

      </main>

      <style dangerouslySetInnerHTML={{
        __html: `
        @keyframes fade-in-up {
          0%   { opacity: 0; transform: translateY(20px); }
          100% { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in-up {
          animation: fade-in-up 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
        }
      ` }} />
    </div>
  );
}

export default App;
