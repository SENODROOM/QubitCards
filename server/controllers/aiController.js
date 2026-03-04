const axios = require('axios');

const GEMINI_URL = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent';

exports.generateCards = async (req, res) => {
  try {
    const { topic, count = 10, difficulty = 'medium' } = req.body;

    if (!topic || !topic.trim()) {
      return res.status(400).json({ success: false, message: 'Topic is required' });
    }

    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey || apiKey === 'YOUR_GEMINI_API_KEY_HERE') {
      return res.status(503).json({ success: false, message: 'Gemini API key not configured. Add it to server/.env' });
    }

    const prompt = `You are an expert educational content creator. Generate exactly ${count} flashcards about "${topic}" at ${difficulty} difficulty level.

IMPORTANT: Respond ONLY with a valid JSON array. No extra text, no markdown, no code blocks.

Format:
[
  {"question": "Question text here?", "answer": "Clear, concise answer here."},
  {"question": "Another question?", "answer": "Another answer."}
]

Requirements:
- Questions should be clear and specific
- Answers should be concise (1-3 sentences max)
- Cover different aspects of the topic
- Make them educational and accurate
- Difficulty: ${difficulty} (easy=basic facts, medium=understanding, hard=analysis/application)

Generate exactly ${count} flashcard pairs:`;

    const response = await axios.post(
      `${GEMINI_URL}?key=${apiKey}`,
      {
        contents: [{ parts: [{ text: prompt }], role: 'user' }],
        generationConfig: { temperature: 0.7, maxOutputTokens: 2048, topP: 0.9 }
      },
      { timeout: 30000 }
    );

    const rawText = response.data?.candidates?.[0]?.content?.parts?.[0]?.text;
    if (!rawText) return res.status(500).json({ success: false, message: 'No response from AI' });

    // Parse JSON from response
    const jsonStart = rawText.indexOf('[');
    const jsonEnd = rawText.lastIndexOf(']');
    if (jsonStart === -1 || jsonEnd === -1) {
      return res.status(500).json({ success: false, message: 'Could not parse AI response' });
    }

    const cards = JSON.parse(rawText.substring(jsonStart, jsonEnd + 1));
    const validCards = cards.filter(c => c.question?.trim() && c.answer?.trim());

    res.json({ success: true, data: validCards, count: validCards.length });
  } catch (err) {
    if (err.response?.status === 400) {
      return res.status(400).json({ success: false, message: 'Invalid request to AI service' });
    }
    if (err.response?.status === 429) {
      return res.status(429).json({ success: false, message: 'AI rate limit reached. Try again in a minute.' });
    }
    res.status(500).json({ success: false, message: err.message || 'AI generation failed' });
  }
};
