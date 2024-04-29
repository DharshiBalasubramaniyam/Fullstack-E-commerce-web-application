import '../auth.css'
import {useRef} from 'react';
import {useForm} from 'react-hook-form';
import { Link} from 'react-router-dom';
import AuthService from '../../../api-service/auth.service';
import Logo from '../../../components/logo/logo';


function Register() {

    const {register, handleSubmit,formState, watch} = useForm();
    const password = useRef({});
    password.current = watch('password', "");
    const {save, isLoading, error} = AuthService()


    const onSubmit = (data) => {
        save(data.username, data.email, data.password)
    }


    return(
        <main className='auth-container'>
            <div className='auth-wrapper'>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <Logo/>
                    <h2>Register now!</h2>
                    {
                        error && <p>{error}</p>
                    }

                    <div className='input-box'>
                        <label>
                            <input 
                                type='text'
                                placeholder='Username'
                                {...register('username', {
                                    required: "Username is required!",
                                    maxLength: {
                                        value: 20,
                                        message: "Username cannot have more than 20 characters!"
                                    },
                                    minLength: {
                                        value: 3,
                                        message: "Username must have atleast 3 characters!"
                                    }
                                })}
                            />
                        </label>
                        
                        {formState.errors.username && <small>{formState.errors.username.message}</small>}
                    </div>
                    
                    
                    <div className='input-box'>
                        <label>
                            <input 
                                type='text'
                                placeholder='Email address'
                                {...register('email', {
                                    required: "Email is required!",
                                    pattern: {value:/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/g, message:"Invalid email address!"}
                                })}
                            />
                        </label>
                        
                        {formState.errors.email && <small>{formState.errors.email.message}</small>}
                    </div>
                    
                    <div className='input-box'>
                        <label>
                            <input 
                                type='password'
                                placeholder='Password'
                                {
                                    ...register('password', {
                                        required: 'Password is required!',
                                        minLength: {
                                            value:8,
                                            message: "Password must have atleast 8 characters!"
                                        },
                                        maxLength: {
                                            value: 20,
                                            message: "Password cannot have more than 20 characters!"
                                        }
                                    })
                                }
                            />
                        </label>
                        
                        {formState.errors.password && <small>{formState.errors.password.message}</small>}
                    </div>

                    <div className='input-box'>
                        <input 
                            type='password'
                            placeholder='Confirm password'
                            {
                                ...register('cpassword', {
                                    required: 'Confirm password is required!',
                                    minLength: {
                                        value:8,
                                        message: "Password must have atleast 8 characters!"
                                    },
                                    maxLength: {
                                        value: 20,
                                        message: "Password cannot have more than 20 characters!"
                                    },
                                    validate: cpass => cpass === password.current || "Passwords do not match!"
                                })
                            }
                        />
                        {formState.errors.cpassword && <small>{formState.errors.cpassword.message}</small>}
                    </div>

                    
                    <div className='input-box'>
                        <input type='submit' value={isLoading ? 'Registering' : 'Register'} className={isLoading ? 'loading' : ''}/>
                    </div>
                    <div className='msg'>By clicking Register, you are agree to our user agreement and privacy policy.</div>
                    <div className='msg'>Already a member? <Link to='/auth/login' className='auth-link'>Login Here</Link></div>
                </form>
            </div>
        </main>
    )
}

export default Register;