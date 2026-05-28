# Contexto: Implementar Fluxo de Logout

Cole este prompt inteiro no Claude rodando no seu projeto frontend.

---

## PROMPT PARA O CLAUDE DO FRONTEND

O login já está implementado e funcionando. Preciso que você implemente o **fluxo completo de logout** neste projeto. Siga estritamente o que está documentado abaixo.

---

### 1. COMO O LOGOUT FUNCIONA NESTA API

Não existe endpoint de logout no servidor. Os tokens JWT expiram naturalmente:
- `accessToken` expira em **30 minutos**
- `refreshToken` expira em **7 dias**

O logout é feito **apenas no cliente**, limpando o estado local. Nenhuma chamada HTTP ao servidor é necessária.

---

### 2. O QUE ESTÁ SALVO NO LOCALSTORAGE APÓS O LOGIN

```js
localStorage.getItem('accessToken')  // JWT de acesso
localStorage.getItem('refreshToken') // JWT de renovação
localStorage.getItem('user')         // JSON com { id, name, nomeEscoteiro, matricula, role, email, fotoUrl, grupoId }
```

---

### 3. FUNÇÃO DE LOGOUT

```js
function logout() {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('user');
  // redirecionar para /login
}
```

---

### 4. ONDE COLOCAR O BOTÃO DE LOGOUT

O botão de logout deve aparecer em **todas as páginas de dashboard**:

| Rota | Perfil |
|---|---|
| `/dashboard/lobinho` | Usuário lobinho |
| `/dashboard/escotista` | Usuário escotista |
| `/dashboard/dirigente` | Usuário dirigente |

O botão pode ficar no header, navbar, sidebar ou onde fizer mais sentido visualmente no layout já existente.

---

### 5. COMPORTAMENTO ESPERADO

1. Usuário clica em "Sair" (ou equivalente)
2. As 3 chaves do `localStorage` são removidas
3. Usuário é redirecionado para `/login`
4. Se tentar acessar qualquer rota `/dashboard/*` após o logout, deve ser redirecionado de volta para `/login` (o guard de rota já deve fazer isso)

---

### 6. CHECKLIST

- [ ] Criar função `logout()` centralizada (não repetir a lógica em cada dashboard)
- [ ] Adicionar botão "Sair" em `/dashboard/lobinho`
- [ ] Adicionar botão "Sair" em `/dashboard/escotista`
- [ ] Adicionar botão "Sair" em `/dashboard/dirigente`
- [ ] Após logout, redirecionar para `/login`
- [ ] Confirmar que o guard de rota bloqueia o acesso ao dashboard após o logout
