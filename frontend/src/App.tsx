import React from 'react';
import { PublishMessageTile } from './PublishMessageTile';

function App() {
  const rec = new Set<string>();
  rec.add("facebook");
  return (
    <div className="App">
      <PublishMessageTile message="Test Message" receivers = {rec}/>
    </div>
  );
}

export default App;
