package com.cinevault.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): FirebaseUser {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user ?: throw Exception("Usuário não encontrado")
        } catch (e: FirebaseAuthInvalidUserException) {
            throw Exception("E-mail não cadastrado.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("Senha incorreta.")
        } catch (e: Exception) {
            throw Exception("Erro ao fazer login: ${e.localizedMessage}")
        }
    }

    suspend fun register(email: String, password: String, name: String): FirebaseUser {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Falha ao criar usuário")
            
            db.collection("users").document(user.uid).set(
                mapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "displayName" to name,
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()
            
            user
        } catch (e: FirebaseAuthUserCollisionException) {
            throw Exception("Este e-mail já está em uso.")
        } catch (e: Exception) {
            throw Exception("Erro no cadastro: ${e.localizedMessage}")
        }
    }

    fun logout() = auth.signOut()
}
