import '../auth.css'
import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {useForm} from 'react-hook-form';
import AuthService from '../../../api-service/auth.service';
import Logo from '../../../components/logo/logo';

function RegistrationVerfication() {

    const { email } = useParams(); 
    const navigate = useNavigate();
    const {register, handleSubmit, formState} = useForm();
    const {verifyRegistration, resendVerificationCode, isLoading, error} = AuthService()


    const onVerify = (data) => {
        verifyRegistration(data.code)
    }

    const onResend = () => {
        resendVerificationCode(email)
    }

    return(
        <main className='auth-container'>
            <div className='auth-wrapper'>
                <form onSubmit={handleSubmit(onVerify)} style={{gap:'15px'}}>
                    <Logo/>
                    <h2>Verify your email</h2>

                    {
                        (error) && <p>{error}</p>
                    }

                    <div className='input-box'>
                        <label>
                            <input 
                                placeholder='Enter verification code'
                                    type='text'
                                    {...register('code', {
                                        required: "Verification code is required!",
                                    })}
                            />
                        </label>
                        
                        {formState.errors.code && <small>{formState.errors.code.message}</small>}
                    </div>
                    
                    <div className='msg' style={{fontWeight: 600, fontStyle: 'italic'}}>Please note that the verification code will be expired with in 15 minutes!</div>
                    
                    <div className='input-box'>
                        <input type='submit' value={isLoading ? 'Verifying' : 'Verify'} className={isLoading ? 'loading' : ''}/>
                    </div>

                    <div className='msg'>Having problems? <span className='auth-link' onClick={onResend}>Resend code</span></div>
                </form>
            </div>
        </main>
    )
}

export default RegistrationVerfication;