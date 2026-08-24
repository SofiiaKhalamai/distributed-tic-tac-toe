const SESSION_SERVICE_BASE = 'http://localhost:8082';
const MOVE_ANIMATION_DELAY_MS = 400;

const startBtn = document.getElementById('start-btn');
const statusText = document.getElementById('status-text');
const boardEl = document.getElementById('board');
const moveLogEl = document.getElementById('move-log');
const errorPanel = document.getElementById('error-panel');

function renderEmptyBoard() {
    boardEl.innerHTML = '';
    for (let i = 0; i < 9; i++) {
        const cell = document.createElement('div');
        cell.className = 'cell';
        cell.dataset.index = i;
        boardEl.appendChild(cell);
    }
}

function setCell(row, col, symbol) {
    const index = row * 3 + col;
    const cell = boardEl.children[index];
    cell.textContent = symbol;
    cell.classList.add(symbol.toLowerCase());
}

function logMove(move) {
    const li = document.createElement('li');
    li.textContent = `#${move.sequence}: ${move.symbol} -> (${move.row}, ${move.col})`;
    moveLogEl.appendChild(li);
}

function describeStatus(gameStatus) {
    switch (gameStatus) {
        case 'IN_PROGRESS': return 'In progress...';
        case 'X_WON': return 'X wins!';
        case 'O_WON': return 'O wins!';
        case 'DRAW': return 'Draw';
        default: return gameStatus;
    }
}

function showError(message) {
    errorPanel.textContent = message;
    errorPanel.classList.remove('hidden');
}

function clearError() {
    errorPanel.classList.add('hidden');
    errorPanel.textContent = '';
}

async function requestJson(url, options) {
    const response = await fetch(url, options);
    if (!response.ok) {
        const body = await response.json().catch(() => null);
        throw new Error(body?.message || `Request failed with status ${response.status}`);
    }
    return response.json();
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function replayMoves(moveHistory, finalGameStatus) {
    renderEmptyBoard();
    moveLogEl.innerHTML = '';
    for (const move of moveHistory) {
        await sleep(MOVE_ANIMATION_DELAY_MS);
        setCell(move.row, move.col, move.symbol);
        logMove(move);
    }
    statusText.textContent = describeStatus(finalGameStatus);
}

async function startSimulation() {
    clearError();
    startBtn.disabled = true;
    moveLogEl.innerHTML = '';
    statusText.textContent = 'Starting session...';
    renderEmptyBoard();

    try {
        const session = await requestJson(`${SESSION_SERVICE_BASE}/sessions`, { method: 'POST' });
        statusText.textContent = 'Simulating...';

        const result = await requestJson(`${SESSION_SERVICE_BASE}/sessions/${session.sessionId}/simulate`, { method: 'POST' });
        await replayMoves(result.moveHistory, result.gameStatus);
    } catch (err) {
        showError(err.message);
        statusText.textContent = 'Failed';
    } finally {
        startBtn.disabled = false;
    }
}

startBtn.addEventListener('click', startSimulation);
renderEmptyBoard();
