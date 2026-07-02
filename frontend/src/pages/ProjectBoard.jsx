import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../api'
import { useAuth } from '../context/AuthContext'

const STATUSES = [
  { key: 'TODO', label: 'To do', columnClass: 'col-todo' },
  { key: 'IN_PROGRESS', label: 'In progress', columnClass: 'col-progress' },
  { key: 'DONE', label: 'Done', columnClass: 'col-done' },
]

export default function ProjectBoard() {
  const { id } = useParams()
  const { username } = useAuth()

  const [project, setProject] = useState(null)
  const [tasks, setTasks] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [dueDate, setDueDate] = useState('')
  const [creating, setCreating] = useState(false)

  const [memberUsername, setMemberUsername] = useState('')
  const [addingMember, setAddingMember] = useState(false)

  useEffect(() => {
    loadAll()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  async function loadAll() {
    setLoading(true)
    setError('')
    try {
      const [projectRes, tasksRes] = await Promise.all([
        api.get(`/projects/${id}`),
        api.get(`/projects/${id}/tasks`),
      ])
      setProject(projectRes.data)
      setTasks(tasksRes.data)
    } catch (err) {
      setError('Could not load this project.')
    } finally {
      setLoading(false)
    }
  }

  async function handleCreateTask(e) {
    e.preventDefault()
    if (!title.trim()) return
    setCreating(true)
    try {
      const { data } = await api.post(`/projects/${id}/tasks`, {
        title,
        description,
        dueDate: dueDate || null,
      })
      setTasks((prev) => [...prev, data])
      setTitle('')
      setDescription('')
      setDueDate('')
    } catch (err) {
      setError('Could not create the task.')
    } finally {
      setCreating(false)
    }
  }

  async function handleStatusChange(taskId, status) {
    const previous = tasks
    setTasks((prev) => prev.map((t) => (t.id === taskId ? { ...t, status } : t)))
    try {
      await api.patch(`/tasks/${taskId}/status`, { status })
    } catch (err) {
      setTasks(previous)
      setError('Could not update task status.')
    }
  }

  async function handleDeleteTask(taskId) {
    const previous = tasks
    setTasks((prev) => prev.filter((t) => t.id !== taskId))
    try {
      await api.delete(`/tasks/${taskId}`)
    } catch (err) {
      setTasks(previous)
      setError('Could not delete the task.')
    }
  }

  async function handleAddMember(e) {
    e.preventDefault()
    if (!memberUsername.trim()) return
    setAddingMember(true)
    try {
      const { data } = await api.post(`/projects/${id}/members`, { username: memberUsername })
      setProject(data)
      setMemberUsername('')
    } catch (err) {
      setError(err.response?.data?.message || 'Could not add that member.')
    } finally {
      setAddingMember(false)
    }
  }

  if (loading) {
    return (
      <div className="app-shell">
        <main className="main"><p>Loading project…</p></main>
      </div>
    )
  }

  if (!project) {
    return (
      <div className="app-shell">
        <main className="main">
          <Link to="/" className="back-link">← Back to projects</Link>
          <div className="error-banner">{error || 'Project not found.'}</div>
        </main>
      </div>
    )
  }

  const isOwner = project.owner?.username === username

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">Ledger</div>
        <div className="brand-sub">Task Manager</div>
        <div className="sidebar-section-label">Navigate</div>
        <ul className="project-list">
          <li><Link to="/">← All projects</Link></li>
        </ul>
      </aside>

      <main className="main">
        <Link to="/" className="back-link">← Back to projects</Link>

        <div className="page-header">
          <div>
            <div className="page-eyebrow">{isOwner ? 'Owner' : 'Member'}</div>
            <h1>{project.name}</h1>
          </div>
        </div>

        {error && <div className="error-banner">{error}</div>}

        {isOwner && (
          <div className="members-box">
            <h3>Team members</h3>
            <div className="member-chips">
              <span className="member-chip">{project.owner.username} (owner)</span>
              {project.members?.map((m) => (
                <span className="member-chip" key={m.id}>{m.username}</span>
              ))}
            </div>
            <form className="add-member-row" onSubmit={handleAddMember}>
              <input
                placeholder="Username to add"
                value={memberUsername}
                onChange={(e) => setMemberUsername(e.target.value)}
              />
              <button className="btn btn-ghost" type="submit" disabled={addingMember}>
                {addingMember ? 'Adding…' : 'Add'}
              </button>
            </form>
          </div>
        )}

        <form className="new-task-form" onSubmit={handleCreateTask}>
          <div className="field grow">
            <label htmlFor="ttitle">New task</label>
            <input id="ttitle" placeholder="Task title" value={title} onChange={(e) => setTitle(e.target.value)} />
          </div>
          <div className="field grow">
            <label htmlFor="tdesc">Description</label>
            <input id="tdesc" placeholder="Optional" value={description} onChange={(e) => setDescription(e.target.value)} />
          </div>
          <div className="field">
            <label htmlFor="tdue">Due date</label>
            <input id="tdue" type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
          </div>
          <button className="btn" type="submit" disabled={creating}>
            {creating ? 'Adding…' : 'Add task'}
          </button>
        </form>

        <div className="board">
          {STATUSES.map((col) => (
            <div className={`board-column ${col.columnClass}`} key={col.key}>
              <div className="board-column-header">
                <span className="label">{col.label}</span>
                <span className="count">{tasks.filter((t) => t.status === col.key).length}</span>
              </div>
              <div className="board-column-body">
                {tasks.filter((t) => t.status === col.key).length === 0 && (
                  <p style={{ color: 'var(--ink-soft)', fontSize: 12.5 }}>Nothing here yet.</p>
                )}
                {tasks
                  .filter((t) => t.status === col.key)
                  .map((task) => (
                    <div className="task-card" key={task.id}>
                      <h4>{task.title}</h4>
                      {task.description && <p>{task.description}</p>}
                      <div className="task-meta">
                        <span>{task.dueDate ? `due ${task.dueDate}` : 'no due date'}</span>
                        <select
                          value={task.status}
                          onChange={(e) => handleStatusChange(task.id, e.target.value)}
                        >
                          {STATUSES.map((s) => (
                            <option key={s.key} value={s.key}>{s.label}</option>
                          ))}
                        </select>
                      </div>
                      <div className="task-actions">
                        <button className="btn btn-danger" onClick={() => handleDeleteTask(task.id)}>
                          Delete
                        </button>
                      </div>
                    </div>
                  ))}
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  )
}
