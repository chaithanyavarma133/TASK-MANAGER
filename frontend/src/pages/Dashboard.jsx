import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api'
import { useAuth } from '../context/AuthContext'

export default function Dashboard() {
  const { username, logout } = useAuth()
  const navigate = useNavigate()
  const [projects, setProjects] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [creating, setCreating] = useState(false)

  useEffect(() => {
    loadProjects()
  }, [])

  async function loadProjects() {
    setLoading(true)
    try {
      const { data } = await api.get('/projects')
      setProjects(data)
    } catch (err) {
      setError('Could not load your projects.')
    } finally {
      setLoading(false)
    }
  }

  async function handleCreate(e) {
    e.preventDefault()
    if (!name.trim()) return
    setCreating(true)
    try {
      const { data } = await api.post('/projects', { name, description })
      setName('')
      setDescription('')
      setProjects((prev) => [...prev, data])
    } catch (err) {
      setError('Could not create the project.')
    } finally {
      setCreating(false)
    }
  }

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">Ledger</div>
        <div className="brand-sub">Task Manager</div>

        <div className="sidebar-section-label">Projects</div>
        <ul className="project-list">
          {projects.map((p) => (
            <li key={p.id}>
              <Link to={`/projects/${p.id}`}>{p.name}</Link>
            </li>
          ))}
        </ul>

        <div className="sidebar-footer">
          <div className="sidebar-user">Signed in as {username}</div>
          <button className="btn btn-ghost btn-block" onClick={handleLogout}>Sign out</button>
        </div>
      </aside>

      <main className="main">
        <div className="page-header">
          <div>
            <div className="page-eyebrow">Overview</div>
            <h1>Your projects</h1>
          </div>
        </div>

        {error && <div className="error-banner">{error}</div>}

        <form className="new-project-form" onSubmit={handleCreate}>
          <div className="field">
            <label htmlFor="pname">New project</label>
            <input id="pname" placeholder="Project name" value={name} onChange={(e) => setName(e.target.value)} />
          </div>
          <div className="field">
            <label htmlFor="pdesc">Description</label>
            <input id="pdesc" placeholder="Optional" value={description} onChange={(e) => setDescription(e.target.value)} />
          </div>
          <button className="btn" type="submit" disabled={creating}>
            {creating ? 'Creating…' : 'Create project'}
          </button>
        </form>

        {loading ? (
          <p>Loading projects…</p>
        ) : projects.length === 0 ? (
          <div className="empty-state">No projects yet. Create your first one above.</div>
        ) : (
          <div className="project-grid">
            {projects.map((p) => (
              <Link key={p.id} to={`/projects/${p.id}`} className="project-card">
                <span className="stamp">{p.owner?.username === username ? 'owner' : 'member'}</span>
                <h3>{p.name}</h3>
                <p>{p.description || 'No description'}</p>
              </Link>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}
